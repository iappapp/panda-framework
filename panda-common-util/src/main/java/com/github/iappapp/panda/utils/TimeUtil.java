package com.github.iappapp.panda.utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间处理方面的工具。
 *
 * @author Administrator
 */
public final class TimeUtil {

    /**
     * 把UNIX时间戳转换为类似2009-11-09 11:31:26这种格式的java字符串。
     *
     * @param timestampString String类型的Unix时间戳
     */
    public static String timeStamp2DateStr(String timestampString) {
        Long timestamp = Long.parseLong(timestampString) * 1000;
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(timestamp));
    }

    /**
     * 把UNIX时间戳转换为类似2009-11-09 11:31:26这种格式的java字符串。
     *
     * @param timestamp1 long类型的Unix时间戳
     */
    public static String timeStamp2Str(long timestamp1) {
        Long timestamp = timestamp1 * 1000;
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(timestamp));
    }

    /**
     * 把UNIX时间戳转换为对应的Java Date对象
     *
     * @param timestampString 实际为long类型的值的UNIX时间戳
     */
    public static Date timeStamp2Date(String timestampString) {
        Long timestamp = Long.parseLong(timestampString) * 1000L;
        return new Date(timestamp);
    }

    /**
     * 把UNIX时间戳转换为对应的Java Date对象
     */
    public static Date timeStamp2Date(long timestamp) {
        Long timeStamp = timestamp * 1000L;
        return new Date(timeStamp);
    }

    public static long dateStrToSec(String dateStr) throws ParseException {
        return (new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .parse(dateStr)).getTime() / 1000;
    }

    public static long getDateStrToSec(String dateStr) throws ParseException {
        return (new java.text.SimpleDateFormat("yyyyMMddHHmmss").parse(dateStr))
                .getTime() / 1000;
    }

    /**
     * 获得获得系统的UNIX时间戳的值
     *
     * @return
     */
    public static long getUnixTimestamp() {
        return System.currentTimeMillis() / 1000L;
    }

    public static String getTimeLong(long time) {
        String s = "%d天%d小时%d分%d秒";
        int day = (int) (time / (24 * 3600 * 1000l));
        int hour = (int) ((time % (24 * 3600 * 1000l)) / (3600 * 1000));
        int min = (int) (((time % (24 * 3600 * 1000l)) % (3600 * 1000)) / (60 * 1000));
        int sec = (int) ((((time % (24 * 3600 * 1000l)) % (3600 * 1000)) % (60 * 1000)) / (1000));

        return String.format(s, day, hour, min, sec);
    }

    public static long getTodayStartSecond() {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        System.out.println(ca.getTime());
        return ca.getTimeInMillis() / 1000;
    }

    public static long getTodayEndSecond() {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, 23);
        ca.set(Calendar.MINUTE, 59);
        ca.set(Calendar.SECOND, 59);
        System.out.println(ca.getTime());
        return ca.getTimeInMillis() / 1000;
    }

    /**
     * 获取一天的开始时间0点整
     *
     * @param timeStart
     * @return
     */
    public static Date getStartTime(Date timeStart) {
        Calendar ca = Calendar.getInstance();
        if (timeStart != null) {
            ca.setTime(timeStart);
        }
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        return ca.getTime();
    }

    /**
     * 获取一天的开始结束0点整
     *
     * @param timeEnd
     * @return
     */
    public static Date getEndTime(Date timeEnd) {
        Calendar ca = Calendar.getInstance();
        if (timeEnd != null) {
            ca.setTime(timeEnd);
        }
        ca.set(Calendar.HOUR_OF_DAY, 23);
        ca.set(Calendar.MINUTE, 59);
        ca.set(Calendar.SECOND, 59);
        return ca.getTime();
    }

    /**
     * 通知邮箱的截止时间
     */
    public static Date getAbortTime(Date time) {
        Calendar ca = Calendar.getInstance();
        if (time != null) {
            ca.setTime(time);
        }
        int DAY_OF_MONTH = ca.get(Calendar.DAY_OF_MONTH);
        if (DAY_OF_MONTH <= 15) {
            ca.add(Calendar.MONTH, 1);
            ca.set(Calendar.DAY_OF_MONTH, 0);
            ca.set(Calendar.HOUR_OF_DAY, 23);
            ca.set(Calendar.MINUTE, 59);
            ca.set(Calendar.SECOND, 59);
        } else {
            ca.add(Calendar.MONTH, 2);
            ca.set(Calendar.DAY_OF_MONTH, 0);
            ca.set(Calendar.HOUR_OF_DAY, 23);
            ca.set(Calendar.MINUTE, 59);
            ca.set(Calendar.SECOND, 59);
        }
        return ca.getTime();
    }
}
