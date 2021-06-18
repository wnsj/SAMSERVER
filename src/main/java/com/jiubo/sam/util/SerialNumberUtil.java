package com.jiubo.sam.util;

import com.jiubo.sam.exception.MessageException;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class SerialNumberUtil {

    public static String generateSerialNumber(LocalDateTime dateTime,String typeLabel,Integer count) throws MessageException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String format = dateTime.format(formatter);
        count = count == null ? 1 : count+1;

        int length = String.valueOf(count).length();
        if (length == 1) {
            return "SA" + format + typeLabel + "000" + count;
        } else if (length == 2) {
            return "SA" + format + typeLabel + "00" + count;
        } else if (length == 3) {
            return "SA" + format + typeLabel + "0" + count;
        } else if (length == 4) {
            return "SA" + format + typeLabel + count;
        } else {
            throw new MessageException("流水号长度有误，请联系管理员");
        }
    }
}


