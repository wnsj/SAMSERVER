package com.jiubo.sam.util;


import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IDCardUtils {
    public static String evaluate(String idCard) {

        if (!StringUtils.isNotBlank(idCard)) {
            return "身份证件号为空,无法计算年龄";
        }

        if (idCard.length() != 15 && idCard.length() != 18) {
            return "身份证件号有误,无法计算年龄";
        }

        String birthDay = idCard.substring(6, 14);
        String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String yearStr = time.split("-")[0];
        String monthStr = time.split("-")[1];
        String dayStr = time.split("-")[2];
        String yearBirthStr = birthDay.substring(0, 4);
        String monthBirthStr = birthDay.substring(4, 6);
        String dayBirthStr = birthDay.substring(6);
        int year = Integer.parseInt(yearStr);
        int yearBirth = Integer.parseInt(yearBirthStr);

        if (year - yearBirth != 0) {
            int yyyy = year - yearBirth;
            return yyyy + "岁";
        }

        int month = Integer.parseInt(monthStr);
        int monthBirth = Integer.parseInt(monthBirthStr);
        if (month - monthBirth != 0) {
            int MM = month - monthBirth;
            return MM + "个月";
        }

        int day = Integer.parseInt(dayStr);
        int dayBirth = Integer.parseInt(dayBirthStr);
        return Math.max(day - dayBirth, 0) + "天";
    }
}
