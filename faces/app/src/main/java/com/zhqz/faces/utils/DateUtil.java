package com.zhqz.faces.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class DateUtil {

    public static Calendar CALENDAR = Calendar.getInstance();

    public static int getYEAR() {
        CALENDAR.setTimeInMillis(System.currentTimeMillis());
        return CALENDAR.get(Calendar.YEAR);
    }

    public static int getMONTH() {
        CALENDAR.setTimeInMillis(System.currentTimeMillis());
        return CALENDAR.get(Calendar.MONTH)+1 ;
    }

    public static int getDAY_OF_MONTH() {
        CALENDAR.setTimeInMillis(System.currentTimeMillis());
        return CALENDAR.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前时间,format = yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getCurrentTime() {
        return getNow("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取当前时间,format = HH:mm:ss
     *
     * @return
     */
    public static String getTimeHHmmss() {
        return getNow("HH:mm:ss");
    }

    public static String getTimeyyyyMMdd() {
        return getNow("yyyy-MM-dd");
    }

    /**
     * 根据指定的format格式获取当前时间
     *
     * @param format
     * @return
     */
    public static String getNow(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date(System.currentTimeMillis());
        return dateFormat.format(date);
    }

    public static String getHHmmss_time(int time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        Date date = new Date(time);
        return dateFormat.format(date);
    }

    /**
     * 时间转换为时间戳
     *
     * @param timeStr 时间 例如: 2016-03-09
     * @return
     */
    public static long getTimeStamp(String timeStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(timeStr);
            long timeStamp = date.getTime();
            return timeStamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16时09分00秒"）
     *
     * @param time
     * @return
     */
    public static String times(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;

    }

    /**
     * 获取星期
     *
     * @return
     */
    public static String getWeek() {
        String mWay = "";
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "星期日";
        } else if ("2".equals(mWay)) {
            mWay = "星期一 ";
        } else if ("3".equals(mWay)) {
            mWay = "星期二 ";
        } else if ("4".equals(mWay)) {
            mWay = "星期三";
        } else if ("5".equals(mWay)) {
            mWay = "星期四 ";
        } else if ("6".equals(mWay)) {
            mWay = "星期五";
        } else if ("7".equals(mWay)) {
            mWay = "星期六";
        }
        return mWay;
    }

    /**
     * 日期变量转成对应的星期字符串
     *
     * @param date
     * @return
     */
    public static String DateToWeek(Date date) {
        String[] WEEK = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayIndex < 1 || dayIndex > 7) {
            return null;
        }
        return WEEK[dayIndex - 1];
    }


    /**
     * 获取当前日期月份
     *
     * @param date
     * @return
     */
    public static int getMonth(String date) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date nowDate = null;
        nowDate = df.parse(date, pos);
        calendar.setTime(nowDate);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 判断日期大小
     */
    public static long compare_date(long time1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date dt2 = df.parse(DATE2, pos);

        long time2 = dt2.getTime();

        long jieshutime = time2 - time1;

        return jieshutime;
    }

    /**
     * 判断日期大小
     */
    public static long compare_date_two(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos1 = new ParsePosition(0);
        Date dt1 = df.parse(DATE1, pos1);
        long time1 = dt1.getTime();
        
        ParsePosition pos2 = new ParsePosition(0);
        Date dt2 = df.parse(DATE2, pos2);
        long time2 = dt2.getTime();

        long timecha = time1 - time2;

        return timecha;
    }

    public static Calendar ToDate() {
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = null;
        try {
            dt = sdf.parse(DateUtil.getTimeyyyyMMdd());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.print(sdf.format(dt));    //输出结果是：2005-2-19
        cal.setTime(dt);
        return cal;
    }

    /**
     * 获取指定日后 后 dayAddNum 天的 日期
     *
     * @param day       ，格式为String："2013-9-3";
     * @param dayAddNum dayAddNum 增加天数 格式为int;
     * @return
     */
    public static String getDateAddStr(String day, int dayAddNum) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date nowDate = null;
        nowDate = df.parse(day, pos);
        calendar.setTime(nowDate);
        calendar.add(Calendar.DAY_OF_YEAR, dayAddNum);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
    public static int compare_datetwo(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        ParsePosition pos1 = new ParsePosition(0);
        Date dt1 = df.parse(DATE1, pos);
        Date dt2 = df.parse(DATE2, pos1);

        long time1 = dt2.getTime() - (1000 * 60 * 10);
        long time2 = dt2.getTime() + (1000 * 60 * 45);

        if (dt1.getTime() > time2) {
            return 1;//DATE1>DATE2
        } else if (dt1.getTime() < time1) {
            return -1;
        } else {
            return 0;
        }
    }
    public static String getNowYMd() {
        return getNow("yyyy-MM-dd");
    }
}
