package com.jiubo.sam.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class LocalDateTimeUtil {
    public static List<String> findDaysStr(String begintTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dBegin = null;
        Date dEnd = null;
        try {
            dBegin = sdf.parse(begintTime);
            dEnd = sdf.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<String> daysStrList = new ArrayList<String>();
        daysStrList.add(sdf.format(dBegin));
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(dEnd);
        while (dEnd.after(calBegin.getTime())) {
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            String dayStr = sdf.format(calBegin.getTime());
            daysStrList.add(dayStr);
        }
        return daysStrList;
    }

    public static void main(String[] args) {
        List<String> daysStr = LocalDateTimeUtil.findDaysStr("2021-3-6", "2021-3-9");
        System.out.println(daysStr);
    }
}
