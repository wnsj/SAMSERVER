package com.jiubo.sam.util;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2018-01-26.
 */
public class DoubleUtil {

    /**
     * 保留两位小数 四舍五入
     *
     * @param d
     * @return
     */
    public static double DoubleRef(double d) {
        DecimalFormat df = new DecimalFormat("#.00");
        return Double.valueOf(df.format(d));
    }

    public static String DoubleRefl(double d) {
        DecimalFormat df = null;
        if (d > 0) {
            df = new DecimalFormat("###,###,##0.00");

        } else {
            df = new DecimalFormat("0");

        }
        return df.format(d);
    }

    public static String DoubleRefSale(double d) {
        DecimalFormat df = null;
        if (d > 0) {
            df = new DecimalFormat("###,###,##0");

        } else {
            df = new DecimalFormat("0");
        }
        return df.format(d);
    }


    //四舍五入保留2位小数,不够2位则补0
    public static String roundByScale(double v, int scale) {
        v = Math.round(v * 100) * 0.01d;
        if (scale < 0) {
            throw new IllegalArgumentException("The   scale   must   be   a   positive   integer   or   zero");
        }
        if (scale == 0) {
            return new DecimalFormat("0").format(v);
        }
        String formatStr = "0.";
        for (int i = 0; i < scale; i++) {
            formatStr = formatStr + "0";
        }
        return new DecimalFormat(formatStr).format(v);
    }
}
