package com.jiubo.sam.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.cxf.endpoint.Client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class WebApiUtil {
    public static Object[] execWebService(String url, String method, Object[] parameters) {
        JaxWsDynamicClientFactory dcfClient = JaxWsDynamicClientFactory.newInstance();

        Client client = dcfClient.createClient(url);
        Object[] objects = new Object[1024];
        try {
            objects = client.invoke(method, parameters);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("url", url);
            jsonObject.put("parameters", parameters);
            jsonObject.put("returnResult", objects);
            WriteStringToFile(jsonObject.toJSONString(), String.valueOf(parameters[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return objects;
    }

    public static void WriteStringToFile(String method, String toFile) {
        Date date = new Date();
        String formatDate = DateUtils.formatDate(date);
        String filePath = "D:\\" + method + "\\" + formatDate + ".txt";
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            PrintWriter pw = new PrintWriter(new FileWriter(filePath));
            pw.println(toFile);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
