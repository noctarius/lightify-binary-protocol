package com.noctarius.lightify;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class Tool {

    public static void main(String[] args) throws Exception {
        JmDNS mdns = JmDNS.create();
        ServiceInfo[] serviceInfos = mdns.list("_http._tcp.local.");
        for (ServiceInfo serviceInfo : serviceInfos) {
            if (LightifyUtils.isLightifyGateway(serviceInfo)) {
                System.out.println(serviceInfo);
            }
        }
    }

}
