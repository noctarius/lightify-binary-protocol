package com.noctarius.lightify.protocol;

import com.noctarius.lightify.StatusListener;
import com.noctarius.lightify.model.Addressable;
import com.noctarius.lightify.model.ColorLight;
import com.noctarius.lightify.model.DimmableLight;
import com.noctarius.lightify.model.Switchable;
import com.noctarius.lightify.model.TunableWhiteLight;
import com.noctarius.lightify.model.Zone;
import com.noctarius.lightify.protocol.packets.DeviceListResponse;
import com.noctarius.lightify.protocol.packets.GetDeviceInfoResponse;
import com.noctarius.lightify.protocol.packets.GetZoneInfoResponse;
import com.noctarius.lightify.protocol.packets.PacketFactory;
import com.noctarius.lightify.protocol.packets.SetColorResponse;
import com.noctarius.lightify.protocol.packets.SetLuminanceResponse;
import com.noctarius.lightify.protocol.packets.SetSoftSwitchResponse;
import com.noctarius.lightify.protocol.packets.SetSwitchResponse;
import com.noctarius.lightify.protocol.packets.SetTemperatureResponse;
import com.noctarius.lightify.protocol.packets.ZoneListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.noctarius.lightify.protocol.LightifyUtils.exceptional;

public final class Lightify {

    private final Logger logger = LoggerFactory.getLogger(Lightify.class);

    private final Map<Long, CompletableFuture<ByteBuffer>> requestRegistry = new ConcurrentHashMap<>();
    private final Queue<ByteBuffer> sendQueue = new ArrayBlockingQueue<>(20);
    private final AtomicLong sequencer = new AtomicLong();

    private final SocketHandler socketHandler;
    private final ScheduledExecutorService scheduler;
    private final PacketFactory packetFactory;
    private final StatusListener statusListener;

    public Lightify(InetAddress address, StatusListener statusListener) {
        this.statusListener = statusListener;
        this.packetFactory = new PacketFactory(this::nextSequence);
        this.scheduler = Executors.newSingleThreadScheduledExecutor(this::newThread);
        this.socketHandler = new SocketHandler(address);
        new Thread(socketHandler).start();
    }

    public void dispose() {
        socketHandler.shutdown.set(true);
    }

    public void deviceInfo(Addressable addressable, Consumer<GetDeviceInfoResponse> handler) {
        if (addressable instanceof Zone) {
            throw new IllegalArgumentException("To request updates for a zone, use zoneInfo method");
        }

        WritablePacket packet = packetFactory.deviceInfo(addressable);
        request(packet, handler);
    }

    public void zoneInfo(Zone zone, Consumer<GetZoneInfoResponse> handler) {
        WritablePacket packet = packetFactory.zoneInfo(zone);
        request(packet, handler);
    }

    public void zones(Consumer<ZoneListResponse> handler) {
        WritablePacket packet = packetFactory.listZones();
        request(packet, handler);
    }

    public void devices(Consumer<DeviceListResponse> handler) {
        WritablePacket packet = packetFactory.listDevices();
        request(packet, handler);
    }

    public void softSwitch(Switchable addressable, boolean on, int millis, Consumer<SetSoftSwitchResponse> handler) {
        WritablePacket packet = packetFactory.setSoftSwitch(addressable, on, millis);
        request(packet, handler);
    }

    public void hardSwitch(Switchable addressable, boolean on, Consumer<SetSwitchResponse> handler) {
        WritablePacket packet = packetFactory.setSwitch(addressable, on);
        request(packet, handler);
    }

    public void rgb(ColorLight light, short red, short green, short blue, int millis, Consumer<SetColorResponse> handler) {
        WritablePacket packet = packetFactory.setColor(light, red, green, blue, millis);
        request(packet, handler);
    }

    public void temperature(TunableWhiteLight light, int temperature, int millis, Consumer<SetTemperatureResponse> handler) {
        WritablePacket packet = packetFactory.setTemperature(light, temperature, millis);
        request(packet, handler);
    }

    public void luminance(DimmableLight light, short luminance, int millis, Consumer<SetLuminanceResponse> handler) {
        WritablePacket packet = packetFactory.setLuminance(light, luminance, millis);
        request(packet, handler);
    }

    @SuppressWarnings({"unchecked"})
    private void request(WritablePacket packet, Consumer<? extends ReadablePacket> handler) {
        long requestId = packet.getRequestId();
        ByteBuffer buffer = encode(packet);

        Consumer<ByteBuffer> responseHandler = responseHandler((Consumer<ReadablePacket>) handler);
        Function<Throwable, ? extends Void> exceptionHandler = exceptionHandler();

        CompletableFuture<ByteBuffer> requestPromise = requestPromise(requestId);
        CompletableFuture<ByteBuffer> timeout = timeoutPromise(requestId, 2, TimeUnit.MINUTES);

        requestPromise.acceptEitherAsync(timeout, responseHandler, scheduler).exceptionally(exceptionHandler);
        sendQueue.add(buffer);
    }

    private Function<Throwable, ? extends Void> exceptionHandler() {
        return throwable -> {
            throwable.printStackTrace();
            return null;
        };
    }

    private Consumer<ByteBuffer> responseHandler(Consumer<ReadablePacket> handler) {
        return buffer -> decode(buffer).accept(handler);
    }

    private CompletableFuture<ByteBuffer> timeoutPromise(long requestId, int timeout, TimeUnit timeUnit) {
        CompletableFuture<ByteBuffer> promise = new CompletableFuture<>();
        scheduler.schedule(() -> {
            requestRegistry.remove(requestId);
            promise.completeExceptionally(new TimeoutException());
        }, timeout, timeUnit);
        return promise;
    }

    private CompletableFuture<ByteBuffer> requestPromise(long requestId) {
        CompletableFuture<ByteBuffer> promise = new CompletableFuture<>();
        requestRegistry.put(requestId, promise);
        return promise;
    }

    private long nextSequence() {
        while (true) {
            long oldValue = sequencer.get();
            long next = oldValue + 1L;
            if (oldValue > 0xFFFFFFFFL) {
                next = 0;
            }
            if (sequencer.compareAndSet(oldValue, next)) {
                return next;
            }
        }
    }

    private Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, "lightify-events");
        thread.setDaemon(true);
        return thread;
    }

    private ByteBuffer encode(WritablePacket packet) {
        int bufferLength = packet.getPacketLength() + 2;
        ByteBuffer buffer = newByteBuffer(bufferLength);
        packet.write(buffer);
        return (ByteBuffer) buffer.flip();
    }

    @SuppressWarnings({"unchecked"})
    private <P extends ReadablePacket> P decode(ByteBuffer buffer) {
        Command command = Command.byId(buffer.get(3));
        return (P) command.newPacketInstance(buffer);
    }

    private ByteBuffer newByteBuffer(int length) {
        return ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);
    }

    private class SocketHandler
            implements Runnable {

        private final InetAddress address;
        private final AtomicBoolean shutdown = new AtomicBoolean();

        private ByteBuffer lastRequest;
        private ByteBuffer buffer = newByteBuffer(10240);

        private SocketHandler(InetAddress address) {
            this.address = address;
        }

        @Override
        public void run() {
            while (!shutdown.get()) {
                Socket socket = exceptional(() -> connect(), e -> {
                    if (statusListener != null) {
                        statusListener.onConnectionFailed();
                    }
                });

                // Fire listener
                if (statusListener != null) {
                    statusListener.onConnectionEstablished();
                }

                try {
                    exceptional(() -> {
                        OutputStream os = socket.getOutputStream();
                        InputStream is = socket.getInputStream();
                        selecting(is, os);
                    });
                } catch (RuntimeException e) {
                    // Make sure the socket is closed
                    exceptional(() -> socket.close(), Exception::printStackTrace);

                    // Fire listener if registered
                    if (statusListener != null) {
                        statusListener.onConnectionLost();
                    }

                    e.printStackTrace();
                }
            }
        }

        private void selecting(InputStream is, OutputStream os) throws IOException {
            boolean requestOngoing = false;
            long requestId = -1;

            while (!shutdown.get()) {
                if (!requestOngoing) {
                    // Something to send?
                    if (lastRequest == null) {
                        lastRequest = sendQueue.poll();
                    }

                    if (lastRequest != null) {
                        requestId = extractRequestId(lastRequest);
                        lastRequest = send(lastRequest, os);
                        if (lastRequest == null) {
                            requestOngoing = true;
                        }
                    }
                }

                if (requestOngoing) {
                    // Request cancelled
                    if (!requestRegistry.containsKey(requestId)) {
                        logger.info("Request cancelled: {}", requestId);
                        requestOngoing = false;
                        continue;
                    }

                    // Try reading
                    requestOngoing = read(is);
                }

                Thread.yield();
            }
        }

        private long extractRequestId(ByteBuffer lastRequest) {
            return Integer.toUnsignedLong(lastRequest.getInt(4));
        }

        private ByteBuffer send(ByteBuffer lastRequest, OutputStream os) throws IOException {
            //System.out.println("Sending packet...");
            os.write(lastRequest.array());
            os.flush();
            return null;
        }

        private boolean read(InputStream is) throws IOException {
            int readable = Math.min(buffer.remaining(), is.available());
            if (readable > 0) {
                int read = is.read(buffer.array(), buffer.position(), readable);
                buffer.position(read);

                int length = Short.toUnsignedInt(buffer.getShort(0));
                int packetLength = length + 2;

                String debug = DatatypeConverter.printHexBinary(Arrays.copyOf(buffer.array(), buffer.position()));
                logger.trace("response: " + debug);

                if (buffer.position() >= packetLength) {
                    ByteBuffer packet = newByteBuffer(packetLength);
                    packet.put(buffer.array(), 0, packetLength);
                    packet.flip();
                    buffer.compact();
                    buffer.rewind();

                    long requestId = Integer.toUnsignedLong(packet.getInt(4));
                    CompletableFuture<ByteBuffer> promise = requestRegistry.remove(requestId);
                    if (promise != null) {
                        promise.complete(packet);
                        return false; // request finished
                    }
                }
            }
            return true; // request ongoing
        }

        private Socket connect() throws IOException {
            if (statusListener != null) {
                statusListener.onConnect();
            }
            buffer.clear();
            return new Socket(address, 4000);
        }
    }
}
