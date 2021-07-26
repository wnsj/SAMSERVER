package com.jiubo.sam.util;


import java.util.Calendar;

public class IDCardUtils {
    public static String evaluate(String idCard) {

        if (idCard == null || "".equals(idCard)) {
            return "身份证件号有误,无法计算年龄";
        }

        if (idCard.length() != 15 && idCard.length() != 18) {
            return "身份证件号有误,无法计算年龄";
        }

        String age;
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayNow = cal.get(Calendar.DATE);

        int year = Integer.parseInt(idCard.substring(6, 10));
        int month = Integer.parseInt(idCard.substring(10, 12));
        int day = Integer.parseInt(idCard.substring(12, 14));

        if ((month < monthNow) || (month == monthNow && day <= dayNow)) {
            age = String.valueOf(yearNow - year);
        } else {
            age = String.valueOf(yearNow - year - 1);
        }
        return age;
    }
}
