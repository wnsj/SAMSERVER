package com.jiubo.sam;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
//        String s = "{}";
//        if (s.contains("\\")) {
//            s = s.replaceAll("\\\\", "");
//            if (s.contains("nt")) {
//                s = s.replaceAll("n", "");
//                s = s.replaceAll("t", "");
//            }
//        }
//        System.out.println("s:" + s);
//        JSONObject jsonObject = JSONObject.parseObject(s, JSONObject.class);
//        System.out.println("jsonObject:" + jsonObject);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("a", "a");
        dataMap.put("b", "b");
        dataMap.put("c", "c");
        dataMap.put("d", "d");

        for (String key : dataMap.keySet()) {
            System.out.println("key:" + key + ",value:" + dataMap.get(key));
        }

        dataMap.forEach((key, value) -> System.out.println("key:" + key + ",value:" + value));

        String[] strings = new String[]{"1", "2", "3"};
        List<String> list = Arrays.asList(strings);
        list.forEach(v -> System.out.println(v));
        list.forEach(System.out::println);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("i`m runing ...");
            }
        };
        new Thread(runnable).start();

        new Thread(() -> {
            System.out.println("i`m play game ...");
        }).start();

        //stream过滤
        List<String> collect = Arrays.stream(strings).filter(v -> "1".equals(v)).collect(Collectors.toList());
        collect.forEach(System.out::println);

        //构造stream流的方式
        Stream<String> stream = Stream.of("xiaoqiang", "xiaoqing", "xiaohua");
        Stream<String> stream2 = Arrays.stream(strings);
        Stream<String> stream3 = list.stream();

        //流之间的转换
        Stream<String> wangcai = Stream.of("wangcai", "xiaoqiang");
        //
        String[] strings1 = wangcai.toArray(String[]::new);
    }

}
