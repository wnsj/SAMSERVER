package com.jiubo.sam.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.cxf.endpoint.Client;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class WebApiUtil {
    public static Object[] execWebService(String url, String method, Object[] parameters) {
        JaxWsDynamicClientFactory dcfClient = JaxWsDynamicClientFactory.newInstance();

        Client client = dcfClient.createClient(url);
        Object[] objects = new Object[1024];
        try {
            objects = client.invoke(method, parameters);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("url", url);
            jsonObject.put("parameters", parameters);
            jsonObject.put("returnResult", objects);
            WriteStringToFile(jsonObject.toJSONString(), String.valueOf(parameters[0]));
        }

        return objects;
    }

    public static void WriteStringToFile(String toFile,String method) {
        Date date = new Date();
        String formatDate = DateUtils.formatDate(date,"yyyy-MM-dd");
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

    public static String ReaderFileToString(String name) throws IOException {
        // 使用ArrayList来存储每行读取到的字符串
        FileInputStream fis = new FileInputStream(name);
                 //将字节流转化为字符流，编码指定为文件保存的编码
                 InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(isr);
                 StringBuilder str = new StringBuilder();
                 String s;
                 //以行为单位读取文件中的信息
                 while((s=br.readLine())!=null){
                     str.append(s);
                     }
                br.close();
                isr.close();
                fis.close();
        return str.toString();
    }
}

