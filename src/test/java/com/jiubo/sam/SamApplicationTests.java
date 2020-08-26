package com.jiubo.sam;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Vector;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class SamApplicationTests {

    @Test
    public void contextLoads() {


    }

    public static void main(String[] args) {
//        Vector v = new Vector();
//        for (int i = 0; i < 25; i++) {
//            v.add(new String[1 * 1024 * 1024]);
//
//            System.out.println("Xmx=" + Runtime.getRuntime().maxMemory() / 1024.0 / 1024 + "M");
//
//            System.out.println("free mem=" + Runtime.getRuntime().freeMemory() / 1024.0 / 1024 + "M");
//
//            System.out.println("total mem=" + Runtime.getRuntime().totalMemory() / 1024.0 / 1024 + "M");
//        }
        //      if (toJson.contains("\\")) {
//                            toJson = toJson.replaceAll("\\\\", "");
//
//                            if (toJson.contains("nt")) {
//                                toJson = toJson.replaceAll("n","");
//                                toJson = toJson.replaceAll("t","");
//                            }
//
//                        }
//                        toJson = toJson.substring(1, toJson.length() - 1);
//                        JSONObject jsonObject = JSONObject.parseObject(toJson);
        String s = "{}";
        if (s.contains("\\")) {
            s = s.replaceAll("\\\\", "");
            if (s.contains("nt")) {
                s = s.replaceAll("n", "");
                s = s.replaceAll("t", "");
            }
        }
        System.out.println("s:" + s);
        JSONObject jsonObject = JSONObject.parseObject(s, JSONObject.class);
        System.out.println("jsonObject:" + jsonObject);

    }

}
