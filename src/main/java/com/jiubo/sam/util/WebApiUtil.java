package com.jiubo.sam.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.cxf.endpoint.Client;

import java.io.*;
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

    public static void WriteStringToFile(String toFile,String method) {
        Date date = new Date();
        String formatDate = DateUtils.formatDate(date,"yyyy-MM-dd HH:mm:ss");
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

    public static String ReaderFileToString(String name) {
        // 使用ArrayList来存储每行读取到的字符串
        StringBuilder s = new StringBuilder();
        String txt = "";
        try {
            FileReader fr = new FileReader(name);
            BufferedReader bf = new BufferedReader(fr);
            String str;
            // 按行读取字符串
            while ((str = bf.readLine()) != null) {
                s.append(str);
            }
            txt = s.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return txt;
    }
}
