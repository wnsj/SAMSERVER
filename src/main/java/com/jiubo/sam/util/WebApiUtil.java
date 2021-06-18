package com.jiubo.sam.util;

import org.apache.cxf.endpoint.Client;

public class WebApiUtil {
    public static Object[] execWebsevice(String url, String method, Object[] parameters) {
        JaxWsDynamicClientFactory dcflient = JaxWsDynamicClientFactory.newInstance();

        Client client = dcflient.createClient(url);
        Object[] objects = new Object[1024];
        try {
            objects = client.invoke(method, parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects;
    }
}
