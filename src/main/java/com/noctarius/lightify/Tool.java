import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class Tool {

    public static void main(String[] args) throws Exception {
        JmDNS mdns = JmDNS.create();
        ServiceInfo[] serviceInfos = mdns.list("_http._tcp.local.");
        for (ServiceInfo serviceInfo : serviceInfos) {
            System.out.println(serviceInfo);
        }
    }

}
