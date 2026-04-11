/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.date.DateField
 *  cn.hutool.core.date.DateTime
 *  cn.hutool.core.date.DateUtil
 *  cn.hutool.core.date.format.FastDateFormat
 *  cn.hutool.core.util.StrUtil
 */
package com.github.iappapp.panda.utils.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.util.StrUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DateUtils extends DateUtil {
    public static final String UTC_WITHOUT_SEPARATOR_PATTERN = "yyyyMMdd'T'HHmmss'Z'";
    public static final FastDateFormat UTC_FORMAT_WITHOUT_SEPARATOR = FastDateFormat.getInstance("yyyyMMdd'T'HHmmss'Z'", TimeZone.getTimeZone("UTC"));

    public static Date parseFromUtcStr(String utcStr) {
        if (StrUtil.isBlank(utcStr)) {
            return null;
        }
        try {
            return UTC_FORMAT_WITHOUT_SEPARATOR.parse(utcStr);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public static Date parse(String dateStr, String format, TimeZone timeZone) {
        if (StrUtil.isBlank(dateStr)) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        if (null != timeZone) {
            simpleDateFormat.setTimeZone(timeZone);
        }
        try {
            return simpleDateFormat.parse(dateStr);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static LocalTime parseToLocalTimeFromUtcStr(String utcStr) {
        Date date = DateUtils.parseFromUtcStr(utcStr);
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalTime();
    }

    public static String formatToUtcStr(Date date) {
        return UTC_FORMAT_WITHOUT_SEPARATOR.format(date);
    }

    public static String formatToUtcStr(long date) {
        return DateUtils.formatToUtcStr(new Date(date));
    }

    public static String formatToUtcStr(Calendar calendar) {
        Date time = calendar.getTime();
        return DateUtils.formatToUtcStr(time);
    }

    public static String format(long timestamp, TimeZone timeZone, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        if (null != timeZone) {
            simpleDateFormat.setTimeZone(timeZone);
        }
        return simpleDateFormat.format(timestamp);
    }

    public static String utcNow() {
        return UTC_FORMAT_WITHOUT_SEPARATOR.format(new Date());
    }

    public static Date today(LocalTime time) {
        if (null == time) {
            return null;
        }
        LocalDateTime today = LocalDateTime.of(LocalDate.now(), time);
        ZoneId zoneId = ZoneId.systemDefault();
        Instant instant = today.atZone(zoneId).toInstant();
        return Date.from(instant);
    }

    public static Date offsetSecondForUtcStr(String utcStr, int offset) {
        Date date = DateUtils.parseFromUtcStr(utcStr);
        if (date == null) {
            return null;
        }
        return DateUtils.offsetSecond(date, offset);
    }

    public static String offsetForUtcStr(String utcStr, DateField field, int offset) {
        Date date = DateUtils.parseFromUtcStr(utcStr);
        if (date == null) {
            return null;
        }
        DateTime result = DateUtils.offset(date, field, offset);
        return DateUtils.formatToUtcStr(result);
    }

    public static Integer countDaysForUtcStr(String startUtcStr, String endUtcStr) {
        Date start = DateUtils.parseFromUtcStr(startUtcStr);
        Date end = DateUtils.parseFromUtcStr(endUtcStr);
        if (start == null || end == null) {
            return null;
        }
        return (int)DateUtils.betweenDay(start, end, true) + 1;
    }

    public static List<String> listDateStrs(Date startDate, Date endDate) {
        ArrayList<String> list = new ArrayList<String>();
        if (endDate.before(startDate)) {
            return list;
        }
        endDate = DateUtils.endOfDay(endDate);
        DateTime tmp = DateUtils.beginOfDay(startDate);
        while (tmp.before(endDate)) {
            list.add(DateUtils.formatDate(tmp));
            tmp = DateUtils.offsetDay(tmp, 1);
        }
        return list;
    }

    public static boolean isUtcStr(String dateStr) {
        Date date;
        try {
            date = DateUtils.parseFromUtcStr(dateStr);
        }
        catch (Exception e) {
            return false;
        }
        return date != null;
    }

    public static boolean isNowInRange(Date startDate, Date endDate) {
        if (null == startDate || null == endDate) {
            return false;
        }
        Date now = new Date();
        return now.after(startDate) && now.before(endDate) || now.equals(startDate) || now.equals(endDate);
    }
}

