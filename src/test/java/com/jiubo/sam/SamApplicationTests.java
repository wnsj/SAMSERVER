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

        //1.构造Stream流的方式
        Stream<String> stream = Stream.of("xiaoqiang", "xiaoqing", "xiaohua");
        Stream<String> stream2 = Arrays.stream(strings);
        Stream<String> stream3 = list.stream();

        //2.Stream流的之间的转换
        Stream<String> wangcai = Stream.of("wangcai", "xiaoqiang");
        //转换成 Array
        String[] strings1 = wangcai.toArray(String[]::new);

        //转换成 Collection
        List<String> collect1 = Stream.of("wangcai", "xiaoqiang").collect(Collectors.toList());
        collect1.forEach(System.out::println);

        ArrayList<String> collect2 = Stream.of("wangcai", "xiaoqiang").collect(Collectors.toCollection(ArrayList::new));
        collect2.forEach(System.out::println);

        Set<String> collect3 = Stream.of("wangcai", "xiaoqiang").collect(Collectors.toSet());
        collect3.forEach(System.out::println);

        Stack<String> collect4 = Stream.of("wangcai", "xiaoqiang").collect(Collectors.toCollection(Stack::new));
        collect4.forEach(System.out::println);

        //转换成 String
        String s = Stream.of("wangcai", "xiaoqiang").collect(Collectors.joining()).toString();
        System.out.println(s);

        //3.Stream流的map使用
        List<String> collect5 = Stream.of("wangcai", "xiaoqiang").map(String::toUpperCase).collect(Collectors.toList());
        collect5.forEach(System.out::println);

        //4.Stream流的filter使用
        String s1 = Stream.of("wangcai", "xiaoqiang").filter(str -> "xiaohua".equals(str)).findAny().orElse("未找到");
        System.out.println(s1);

        //5.Stream流的flatMap使用
        List<String> collect6 = Stream.of("one,two,three,four,six").flatMap(str -> Stream.of(str.split(","))).filter(str -> !"six".equals(str)).collect(Collectors.toList());
        collect6.forEach(System.out::println);

        //6.Stream流的limit使用
        Random random = new Random();
        random.ints().limit(3).forEach(System.out::println);
        //结合skip使用得到需要的数据[skip表示的是扔掉前n个元素]
        List<String> collect7 = Stream.of("one", "two", "three", "four", "six").limit(3).skip(2).collect(Collectors.toList());
        collect7.forEach(System.out::println);

        //7.Stream流的sort使用
        //随机取值排序
        random.ints().limit(3).sorted().forEach(System.out::println);
        //普通排序
        Stream.of(1, 5, 6, 9, 7).sorted((u1, u2) -> u1.compareTo(u2)).limit(3).forEach(System.out::println);
        //优化排序[先获取在排序效率会更高!]
        Stream.of(1, 5, 6, 9, 7).limit(3).sorted((u1, u2) -> u1.compareTo(u2)).forEach(System.out::println);

        //8.Stream流的peek使用【peek对每个元素执行操作并返回一个新的Stream】
        Stream.of("one", "two", "three", "four", "six").filter(str -> str.length() > 3).peek(r -> System.out.println("转换之前:" + r))
                .map(String::toUpperCase).peek(t -> System.out.println("转换后:" + t)).collect(Collectors.toList()).forEach(System.out::println);

        //
    }

}
