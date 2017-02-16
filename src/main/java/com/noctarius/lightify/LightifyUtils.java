import javax.jmdns.ServiceInfo;
import java.util.concurrent.Callable;

public final class LightifyUtils {

    private LightifyUtils() {
    }

    public static boolean isLightifyGateway(ServiceInfo serviceInfo) {
        return serviceInfo.getName().contains("Lightify-");
    }

    public static String extractLightifyUID(String name) {
        return name.replace("Lightify-", "");
    }

    public static <R> R exceptional(Callable<R> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void exceptional(Exceptional exceptional) {
        try {
            exceptional.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface Exceptional {
        void call() throws Exception;
    }
}
