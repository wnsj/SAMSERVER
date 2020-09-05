package com.jiubo.sam;

import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.AccountBean;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.function.Supplier;
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

        //9.Stream流的parallel使用[parallelStream 是流并行处理程序的代替方法。]
        List strList = Arrays.asList("a", "", "c", "", "e", "", " ");
        // 获取a的数量
        long count = strList.parallelStream().filter(st -> "a".equals(st)).count();
        System.out.println("a的个数:" + count);

        //10.Stream流的max/min/distinct使用
        //得到最大最小
        int max = Stream.of("wangcai", "xiaoqiang").mapToInt(String::length).max().getAsInt();
        int min = Stream.of("wangcai", "xiaoqiang").mapToInt(String::length).min().getAsInt();
        System.out.println("max:" + max + ",min:" + min);

        //得到去重之后的数据
        String lines = "good good study day day up";
        Stream.of(lines).flatMap(line -> Stream.of(line.split(" "))).map(String::toUpperCase).distinct().forEach(System.out::println);

        //11.Stream流的Match使用
        //allMatch：Stream 中全部元素符合则返回 true ;
        //anyMatch：Stream 中只要有一个元素符合则返回 true;
        //noneMatch：Stream 中没有一个元素符合则返回 true。

        boolean b = Stream.of(1, 2, 3, 4).allMatch(v -> v > 3);
        System.out.println("是否全都大于3:" + b);
        b = Stream.of(1, 2, 3, 4).anyMatch(v -> v > 3);
        System.out.println("有一个元素大于3:" + b);
        b = Stream.of(1, 2).noneMatch(v -> v > 3);
        System.out.println("没有一个元素大于3:" + b);

        //12.Stream流的reduce使用[reduce 主要作用是把 Stream 元素组合起来进行操作。]
        //字符串连接
        String concat = Stream.of("A", "B", "C", "D").reduce("", String::concat);
        System.out.println("字符串拼接:" + concat);

        //得到最小值
        double minValue = Stream.of(-4.0, 1.0, 3.0, -2.0).reduce(Double.MAX_VALUE, Double::min);
        System.out.println("最小值:" + minValue);

        //求和
        // 求和, 无起始值
        int sumValue = Stream.of(1, 2, 3, 4).reduce(Integer::sum).get();
        System.out.println("有无起始值求和:" + sumValue);
        // 求和, 有起始值
        sumValue = Stream.of(1, 2, 3, 4).reduce(1, Integer::sum);
        System.out.println("有起始值求和:" + sumValue);
        //过滤拼接
        concat = Stream.of("a", "B", "c", "D", "e", "F").filter(x -> x.compareTo("Z") > 0).reduce("", String::concat);
        System.out.println("过滤和字符串连接:" + concat);

        //13.Stream流的iterate使用
        System.out.println("从2开始生成一个等差队列:");
        Stream.iterate(2, n -> n + 2).limit(5).forEach(x -> System.out.print(x + " "));

        //14.Stream流的Supplier使用[通过实现Supplier类的方法可以自定义流计算规则。]
        System.out.println("通过实现Supplier类的方法自定义流计算规则");
        class UserSupplier implements Supplier {
            private int index = 10;
            private Random random = new Random();

            @Override
            public Object get() {
                return String.valueOf(index++).concat(":").concat(String.valueOf(random.nextInt(10)));
            }
        }

        Stream.generate(new UserSupplier()).limit(2).forEach(System.out::println);

        Stream.generate(new Supplier<Object>() {
            private int index = 10;
            private Random random = new Random();

            @Override
            public Object get() {
                return "实现2:".concat(String.valueOf(index++)).concat(":").concat(String.valueOf(random.nextInt(10)));
            }
        }).limit(3).forEach(System.out::println);

        //15.Stream流的groupingBy/partitioningBy使用
        //groupingBy：分组排序；
        //partitioningBy：分区排序。
        List<Map<String, Object>> maps = new ArrayList<>();
        for (int i = 1; i < 16; i++) {
            for (int x = 1; x < 3; x++) {
                Map<String, Object> map = new HashMap<>();
                map.put("age", i);
                map.put("name", "小强" + x);
                maps.add(map);
            }
        }
        System.out.println("maps:" + maps);
        //分组
        Map<Object, List<Map<String, Object>>> ageMap = maps.stream().collect(Collectors.groupingBy(map -> map.get("age")));
        System.out.println("分组:" + ageMap);
        //分区
        Map<Boolean, List<Map<String, Object>>> age = maps.stream().collect(Collectors.partitioningBy(map -> Integer.parseInt(String.valueOf(map.get("age"))) > 5));
        System.out.println("分区:" + age);

        //16.Stream流的summaryStatistics使用[IntSummaryStatistics 用于收集统计信息(如count、min、max、sum和average)的状态对象]
        //得到最大、最小、之和以及平均数。
        List<Integer> numbers = Arrays.asList(1, 5, 7, 3, 9);
        IntSummaryStatistics stats = numbers.stream().mapToInt(x -> x).summaryStatistics();

        System.out.println("列表中最大的数 : " + stats.getMax());
        System.out.println("列表中最小的数 : " + stats.getMin());
        System.out.println("所有数之和 : " + stats.getSum());
        System.out.println("平均数 : " + stats.getAverage());

        //        Instant：瞬时时间。
        //        LocalDate：本地日期，不包含具体时间, 格式 yyyy-MM-dd。
        //        LocalTime：本地时间，不包含日期. 格式 yyyy-MM-dd HH:mm:ss.SSS 。
        //        LocalDateTime：组合了日期和时间，但不包含时差和时区信息。
        //        ZonedDateTime：最完整的日期时间，包含时区和相对UTC或格林威治的时差。

        //1.获取当前的日期时间
        LocalDate nowLocalDate = LocalDate.now();
        //本地时间（不包括时分秒）
        System.out.println("nowLocalDate:" + nowLocalDate);

        LocalDateTime nowLocalDateTime = LocalDateTime.now();
        System.out.println("nowLocalDateTime:" + nowLocalDateTime);

        //获取当前的时间，包括毫秒
        LocalDateTime ldt = LocalDateTime.now();
        System.out.println("当前年:" + ldt.getYear());   //2018
        System.out.println("当前年份天数:" + ldt.getDayOfYear());//172
        System.out.println("当前月:" + ldt.getMonthValue());
        System.out.println("当前时:" + ldt.getHour());
        System.out.println("当前分:" + ldt.getMinute());
        System.out.println("当前时间:" + ldt.toString());

        //2.获取当前的年月日时分秒
        System.out.println("格式化时间: " + ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));

        //4.时间增减
        System.out.println("5天后时间:" + ldt.plusDays(5));

        System.out.println("5天前时间:" + ldt.minusDays(5));

        System.out.println("指定2099年的当前时间:" + ldt.withYear(2099));

        //5.创建指定时间
        LocalDate ld3 = LocalDate.of(2017, Month.NOVEMBER, 17);
        LocalDate ld4 = LocalDate.of(2018, 02, 11);

        //6.时间相差比较
        LocalDate ld = LocalDate.parse("2017-11-17");
        LocalDate ld2 = LocalDate.parse("2018-01-05");
        //具体相差的年月日
        Period p = Period.between(ld, ld2);
        System.out.println("相差年: " + p.getYears() + " 相差月 :" + p.getMonths() + " 相差天:" + p.getDays());
        //相差总数的时间
        System.out.println("相差月份:" + ChronoUnit.MONTHS.between(ld, ld2));
        System.out.println("两月之间的相差的天数   : " + ChronoUnit.DAYS.between(ld, ld2));

        //精度时间相差[Duration 这个类以秒和纳秒为单位建模时间的数量或数量。]
        Instant inst1 = Instant.now();
        System.out.println("当前时间戳 : " + inst1);
        Instant inst2 = inst1.plus(Duration.ofSeconds(10));
        System.out.println("增加之后的时间 : " + inst2);
        System.out.println("相差毫秒 : " + Duration.between(inst1, inst2).toMillis());
        System.out.println("相毫秒 : " + Duration.between(inst1, inst2).getSeconds());

        //时间大小比较
        LocalDateTime ldt4 = LocalDateTime.now();
        LocalDateTime ldt5 = ldt4.plusMinutes(10);
        System.out.println("当前时间是否大于:" + ldt4.isAfter(ldt5));
        System.out.println("当前时间是否小于:" + ldt4.isBefore(ldt5));

        //7.时区时间计算
        //Clock时钟类用于获取当时的时间戳，或当前时区下的日期时间信息。
        Clock clock = Clock.systemUTC();
        System.out.println("当前时间戳 : " + clock.millis());
        Clock clock2 = Clock.system(ZoneId.of("Asia/Shanghai"));
        System.out.println("亚洲上海此时的时间戳:" + clock2.millis());
        Clock clock3 = Clock.system(ZoneId.of("America/New_York"));
        System.out.println("美国纽约此时的时间戳:" + clock3.millis());

        //通过ZonedDateTime类和ZoneId
        ZoneId zoneId = ZoneId.of("America/New_York");
        ZonedDateTime dateTime = ZonedDateTime.now(zoneId);
        System.out.println("美国纽约此时的时间 : " + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        System.out.println("美国纽约此时的时间 和时区: " + dateTime);
    }

}
