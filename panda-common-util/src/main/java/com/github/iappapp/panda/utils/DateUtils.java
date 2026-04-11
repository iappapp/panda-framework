package com.github.iappapp.panda.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author iadore
 * @date 2020-09-07
 *
 * 日期时间工具类
 */
public final class DateUtils {

    public static final long                           ONE_DAY                    = 24 * 60 * 60 * 1000;

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final String DATETIME_FORMAT            = "yyyy-MM-dd HH:mm:ss";

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final String SMALLSHORTTIME_FORMAT = "yyyy-MM-dd HH:mm";
    /**
     * yyyyMMddHHmmss
     */
    public static final String LDATETIME_FORMAT           = "yyyyMMddHHmmss";
    /**
     * yyyyMMddHHmmss
     */
    public static final String LLDATETIME_FORMAT          = "yyyyMMddHHmmssSSS";
    /**
     * yyyy-MM-dd
     */
    public static final String DATE_FORMAT                = "yyyy-MM-dd";
    /**
     * yyyyMMdd
     */
    public static final String LDATE_FORMAT               = "yyyyMMdd";
    /**
     * HHmmss
     */
    public static final String LSHORTTIME_FORMAT          = "HHmmss";
    /**
     * HH:mm:ss
     */
    public static final String SHORTTIME_FORMAT           = "HH:mm:ss";
    /**
     * yyyyMM
     */
    public static final String YEAR_MONTH_FORMAT          = "yyyyMM";
    /**
     * yyMMddHHmmss
     */
    public static final String YEAR_DATE_FORMAT           = "yyMMddHHmmss";

    private static final ThreadLocal<SimpleDateFormat> dateTimeThreadLocal        = new ThreadLocal<SimpleDateFormat>() {
                                                                                      @Override
                                                                                      protected SimpleDateFormat initialValue() {
                                                                                          return new SimpleDateFormat(
                                                                                              DATETIME_FORMAT);
                                                                                      }
                                                                                  };
    private static final ThreadLocal<SimpleDateFormat> lDateTimeThreadLocal       = new ThreadLocal<SimpleDateFormat>() {
                                                                                      @Override
                                                                                      protected SimpleDateFormat initialValue() {
                                                                                          return new SimpleDateFormat(
                                                                                              LDATETIME_FORMAT);
                                                                                      }
                                                                                  };
    private static final ThreadLocal<SimpleDateFormat> llDateTimeThreadLocal      = new ThreadLocal<SimpleDateFormat>() {
                                                                                      @Override
                                                                                      protected SimpleDateFormat initialValue() {
                                                                                          return new SimpleDateFormat(
                                                                                              LLDATETIME_FORMAT);
                                                                                      }
                                                                                  };
    private static final ThreadLocal<SimpleDateFormat> dateThreadLocal            = new ThreadLocal<SimpleDateFormat>() {
                                                                                      @Override
                                                                                      protected SimpleDateFormat initialValue() {
                                                                                          return new SimpleDateFormat(
                                                                                              DATE_FORMAT);
                                                                                      }
                                                                                  };
    private static final ThreadLocal<SimpleDateFormat> lDateThreadLocal           = new ThreadLocal<SimpleDateFormat>() {
                                                                                      @Override
                                                                                      protected SimpleDateFormat initialValue() {
                                                                                          return new SimpleDateFormat(
                                                                                              LDATE_FORMAT);
                                                                                      }
                                                                                  };
    private static final ThreadLocal<SimpleDateFormat> lShortTimeThreadLocal      = new ThreadLocal<SimpleDateFormat>() {
                                                                                      @Override
                                                                                      protected SimpleDateFormat initialValue() {
                                                                                          return new SimpleDateFormat(
                                                                                              LSHORTTIME_FORMAT);
                                                                                      }
                                                                                  };
    private static final ThreadLocal<SimpleDateFormat> shortTimeThreadLocal       = new ThreadLocal<SimpleDateFormat>() {
                                                                                      @Override
                                                                                      protected SimpleDateFormat initialValue() {
                                                                                          return new SimpleDateFormat(
                                                                                              SHORTTIME_FORMAT);
                                                                                      }
                                                                                  };
    private static final ThreadLocal<SimpleDateFormat> yearMonthFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
                                                                                      @Override
                                                                                      protected SimpleDateFormat initialValue() {
                                                                                          return new SimpleDateFormat(
                                                                                              YEAR_MONTH_FORMAT);
                                                                                      }
                                                                                  };

    private static final ThreadLocal<SimpleDateFormat> shortYearMonthFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(
                    YEAR_DATE_FORMAT);
        }
    };

    public static SimpleDateFormat getYearMonthDate() {
        return yearMonthFormatThreadLocal.get();
    }

    /**
     * @param date
     * @return yyyyMMdd
     */
    public static int getLdate(Date date) {
        return Integer.parseInt(formatLShortDate(date));
    }

    /**
     * @param date
     * @return HHmmss
     */
    public static int getLShortTime(Date date) {
        return Integer.parseInt(formatLShortTime(date));
    }

    /**
     * @param date
     * @return yyyyMMddHHmmss
     */
    public static long getLDateTime(Date date) {
        return Long.parseLong(formatLDateTime(date));
    }

    public static boolean isBefore(Date date1, Date date2) {
        return date2.getTime() - date1.getTime() > 0;
    }

    private static Date add(Date date, int zoom, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        if (amount == 0) {
            return date;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(zoom, amount);
        return cal.getTime();
    }

    public static Date addDays(Date date, int amount) {
        return add(date, Calendar.DATE, amount);
    }

    public static Date addHours(Date date, int amount) {
        return add(date, Calendar.HOUR, amount);
    }

    public static Date addMinutes(Date date, int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    public static Date addMonth(Date date, int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    private static Date paser(DateFormat format, String dateString) {
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("日期格式错误:" + dateString);
        }
    }

    public static String format(DateFormat format, Date date) {
        return format.format(date);
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static Date parseDateTime(String date) {
        return paser(dateTimeThreadLocal.get(), date);
    }

    /**
     * yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static Date parseDate(String date) {
        return paser(dateThreadLocal.get(), date);
    }

    public static Date parseDate(String date, String format) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return paser(new SimpleDateFormat(format), date);
    }

    /**
     * @param date
     * @return HH:mm:ss
     */
    public static String formatShortTime(Date date) {
        return format(shortTimeThreadLocal.get(), date);
    }

    /**
     * @param date
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String formatDateTime(Date date) {
        return format(dateTimeThreadLocal.get(), date);
    }

    /**
     * @param date
     * @return yyyy-MM-dd
     */
    public static String formatDate(Date date) {
        return format(dateThreadLocal.get(), date);
    }

    /**
     * @param date
     * @return format
     */
    public static String formatDate(Date date, String format) {
        return format(new SimpleDateFormat(format), date);
    }

    /**
     * @param date
     * @return yyyyMMdd
     */
    public static String formatLDate(Date date) {
        return format(lDateThreadLocal.get(), date);
    }

    /**
     * @param date
     * @return yyyyMMddHHmmss
     */
    public static String formatLDateTime(Date date) {
        return format(lDateTimeThreadLocal.get(), date);
    }

    public static String formatLLDateTime(Date date) {
        return format(llDateTimeThreadLocal.get(), date);
    }

    /**
     * @param date
     * @return HHmmss
     */
    public static String formatLShortTime(Date date) {
        return format(lShortTimeThreadLocal.get(), date);
    }

    /**
     * @param date
     * @return yyyyMMdd
     */
    public static String formatLShortDate(Date date) {
        return format(lDateThreadLocal.get(), date);
    }

    public static Date yesterdayStart() {
        return new Date(todayStart().getTime() - ONE_DAY);
    }

    public static Date yesterdayEnd() {
        return new Date(todayStart().getTime() - 1);
    }

    public static boolean isYesterday(Date startDay) {
        boolean isYesterday = false;
        Date yesterdayStart = yesterdayStart();
        Date yesterdayEnd = yesterdayEnd();
        if (yesterdayStart.getTime() <= startDay.getTime() && yesterdayEnd.getTime() >= startDay.getTime()) {
            isYesterday = true;
        }
        return isYesterday;
    }

    /**
     * The very beginning of today.
     *
     * @return
     */
    public static Date todayStart() {
        return dayStart(new Date());
    }

    public static Date todayEnd() {
        return dayEnd(new Date());
    }

    public static Date dayStart(Date someday) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(someday);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date dayEnd(Date someday) {
        return new Date(dayStart(someday).getTime() + ONE_DAY - 1);
    }

    /**
     * 格式化 yyMMddHHmmss
     * @param date
     * @return str formatted
     */
    public static String formatShortYearDateTime(Date date) {
        return shortYearMonthFormatThreadLocal.get().format(date);
    }

    /**
     * yyMMddHHmmss
     */
    public static String getHeaderDateStr(Date date) {
        return shortYearMonthFormatThreadLocal.get().format(date);
    }
}
