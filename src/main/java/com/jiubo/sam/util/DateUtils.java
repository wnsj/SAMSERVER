/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jiubo.sam.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 * @author jeeplus
 * @version 2014-4-15
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
	
	private static String[] parsePatterns = {
		"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", 
		"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
		"yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}
	/**
	 * 得到当前日期的月份(yyyyMM)
	 */
	public static String getMonthDate(){
		return getDate("yyyyMM");
	}
	/**
	 * 获取当前周(yyyyWeek)
	 */
	public static String getCurrentWeek(){
		  Calendar calendar = Calendar.getInstance();
		  calendar.setFirstDayOfWeek(Calendar.MONDAY);
		  calendar.setTime(new Date());
		  int weekSeq = calendar.get(Calendar.WEEK_OF_YEAR);
		  if(weekSeq<10){
			  return com.jiubo.sam.util.DateUtils.getYear()+"0"+weekSeq;
		  }else{
			  return com.jiubo.sam.util.DateUtils.getYear()+weekSeq;
		  }
	}
	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}
	
	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date, Object... pattern) {
		String formatDate = null;
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString());
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
		}
		return formatDate;
	}
	
	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}
	
	/**
	 * 日期型字符串转化为日期 格式
	 * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", 
	 *   "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
	 *   "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
	 */
	public static Date parseDate(Object str) {
		if (str == null){
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(24*60*60*1000);
	}

	/**
	 * 获取过去的小时
	 * @param date
	 * @return
	 */
	public static long pastHour(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(60*60*1000);
	}
	
	/**
	 * 获取过去的分钟
	 * @param date
	 * @return
	 */
	public static long pastMinutes(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(60*1000);
	}
	
	/**
	 * 转换为时间（天,时:分:秒.毫秒）
	 * @param timeMillis
	 * @return
	 */
    public static String formatDateTime(long timeMillis){
		long day = timeMillis/(24*60*60*1000);
		long hour = (timeMillis/(60*60*1000)-day*24);
		long min = ((timeMillis/(60*1000))-day*24*60-hour*60);
		long s = (timeMillis/1000-day*24*60*60-hour*60*60-min*60);
		long sss = (timeMillis-day*24*60*60*1000-hour*60*60*1000-min*60*1000-s*1000);
		return (day>0?day+",":"")+hour+":"+min+":"+s+"."+sss;
    }
	
	/**
	 * 获取两个日期之间的天数
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static int getDistanceOfTwoDate(Date before, Date after) {
		long beforeTime = before.getTime();
		long afterTime = after.getTime();
		BigDecimal subtract = BigDecimal.valueOf(afterTime).subtract(BigDecimal.valueOf(beforeTime));
		return Integer.parseInt(subtract.divide(BigDecimal.valueOf(1000 * 60 * 60 * 24),0,BigDecimal.ROUND_HALF_UP).toString());
	}
	
	public static String getDayAddorReduce(Date date,int num){
		return DateFormatUtils.format(addDays(date, num),parsePatterns[0]);
	}
	public static String getDayAddorReduce(String date,int num){
		return DateFormatUtils.format(addDays(parseDate(date), num),parsePatterns[0]);
	}
	/**获取过去几天的日期
	 * @param
	 * @throws ParseException
	 */
	public static String getBackUpDate(Date date, String pattern, long day) {
		long ms = date.getTime() - day*24*3600*1000L;
		Date prevDay = new Date(ms);
		return formatDate(prevDay,pattern);
	}
	/**获取过去几小时的日期
	 * @param
	 * @throws ParseException
	 */
	public static Date getBackUpDateTimeHour(Date date, String pattern, long hour) {
		long ms = date.getTime() - hour*3600*1000L;
		Date prevDay = new Date(ms);
		return prevDay;
	}
	/**获取过去几分钟的日期
	 * @param
	 * @throws ParseException
	 */
	public static Date getBackUpDateTimeMin(Date date, String pattern, long min) {
		long ms = date.getTime() - min*60*1000L;
		Date prevDay = new Date(ms);
		return prevDay;
	}
	public static void main(String[] args) throws ParseException {
//		System.out.println(formatDate(parseDate("2010/3/6")));
//		System.out.println(getDate("yyyy年MM月dd日 E"));
//		long time = new Date().getTime()-parseDate("2012-11-19").getTime();
		//formatDateTime(new Date("213213123213123"));
//		System.out.println(time/(24*60*60*1000));
        //System.out.println(getBackUpDate(new Date(),parsePatterns[1],22));
		/*System.out.println(parseDate(getDayAddorReduce(new Date(), 1)));
		System.out.println(getDayAddorReduce("2018-02-03", 2));*/
		/*SimpleDateFormat s = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		s.parse("2019-12-21 21:33:15");*/
		System.out.println(formatDate(parseDate("20191221141803","yyyyMMddHHmmss"),"yyyy-MM-dd HH:mm:ss"));
		/*try{
			String dateStr = "Sun Dec 22 23:26:00 CST 2019";//英式日期格式
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy,HH:mm:ss", Locale.ENGLISH);
			Date d=sdf.parse(dateStr);
			System.out.println(d);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String s = format.format(d);
			System.out.println(s);
		}catch (ParseException e) {
		 e.printStackTrace();
		}
*/
	}
}
