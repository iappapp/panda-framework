package com.github.iappapp.panda.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * @author zhong bo
 * @createTime 2019/10/2
 * @description 符合JSR310的日期时间工具类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public class DateTimeUtils {
    private static DateTimeFormatter isoFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 把日期时间转换成常用字符串
     *
     * @param e： 需要转换的日期时间变量
     * @return 格式 yyyy-MM-dd HH:mm:ss 的日期时间
     */
    public static String formatDateTime(LocalDateTime e) {
        return e.format(isoFmt);
    }

    /**
     * 把yyyy-MM-dd HH:mm:ss格式的字符串转换为LocalDateTime， 如果输入字符串为空返回null，如果格式错误将抛出异常
     *
     * @param e: 表示日期时间的字符串
     * @return
     */
    public static LocalDateTime parseLocalDateTime(String e) {
        if (e != null && !"".equals(e)) {
            return LocalDateTime.parse(e, isoFmt);
        } else {
            return null;
        }
    }

    public static String getUnixTimestamp(LocalDateTime e) {

        return e.atZone(ZoneOffset.of("+8")).toEpochSecond() + "";
    }

    public static String getUnixTimestamp() {

        return getUnixTimestamp(LocalDateTime.now());
    }

    public static String formatDateTime(LocalDateTime e, String pattern) {
        return e.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static Date getDate(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        Date date = Date.from(zdt.toInstant());
        return date;
    }

    public static LocalDate parseLocalDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    public static LocalTime parseLocalTime(String timeStr) {
        return LocalTime.parse(timeStr, TIME_FORMATTER);
    }

    public static String formatLocalDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public static String formatLocalDateTime(LocalDateTime datetime) {
        return datetime.format(DATETIME_FORMATTER);
    }

    public static String formatLocalTime(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }

    /**
     * 日期相隔天数
     *
     * @param startDateInclusive
     * @param endDateExclusive
     * @return
     */
    public static long periodDays(LocalDate startDateInclusive, LocalDate endDateExclusive) {
        return endDateExclusive.toEpochDay() - startDateInclusive.toEpochDay();
    }

    /**
     * 日期相隔天数
     *
     * @param startDateInclusive
     * @param endDateExclusive
     * @return
     */
    public static long periodDays(LocalDateTime startDateInclusive, LocalDateTime endDateExclusive) {
        return endDateExclusive.toLocalDate().toEpochDay() - startDateInclusive.toLocalDate().toEpochDay();
    }

    /**
     * 日期相隔小时
     *
     * @param startInclusive
     * @param endExclusive
     * @return
     */
    public static long durationHours(Temporal startInclusive, Temporal endExclusive) {
        return Duration.between(startInclusive, endExclusive).toHours();
    }

    /**
     * 日期相隔分钟
     *
     * @param startInclusive
     * @param endExclusive
     * @return
     */
    public static long durationMinutes(Temporal startInclusive, Temporal endExclusive) {
        return Duration.between(startInclusive, endExclusive).toMinutes();
    }

    /**
     * 日期相隔毫秒数
     *
     * @param startInclusive
     * @param endExclusive
     * @return
     */
    public static long durationMillis(Temporal startInclusive, Temporal endExclusive) {
        return Duration.between(startInclusive, endExclusive).toMillis();
    }

    /**
     * 返回当前的日期
     *
     * @return
     */
    public static LocalDate getCurrentLocalDate() {
        return LocalDate.now();
    }

    /**
     * 返回当前时间
     *
     * @return
     */
    public static LocalTime getCurrentLocalTime() {
        return LocalTime.now();
    }

    public static LocalDateTime get() {
        return LocalDateTime.now();
    }

    public static int getYear() {
        return get().getYear();
    }

    public static LocalDateTime withYear(int year) {
        return get().withYear(year);
    }

    public static int getMonth() {
        return get().getMonthValue();
    }

    public static LocalDateTime firstDayOfThisYear(int year) {
        return withYear(year).with(TemporalAdjusters.firstDayOfYear()).with(LocalTime.MIN);
    }

    /**
     * @param year
     * @return String
     * @Title: getFirstDayOfThisYear
     * @Description: 获取设置所属年最初时间
     */
    public static String getFirstDayOfThisYear(int year) {
        LocalDateTime firstDayOfThisYear = firstDayOfThisYear(year);
        return DATETIME_FORMATTER.format(firstDayOfThisYear);
    }

    public static LocalDateTime lastDayOfThisYear(int year) {
        return withYear(year).with(TemporalAdjusters.lastDayOfYear()).with(LocalTime.MAX);
    }

    /**
     * @param year
     * @return String
     * @Title: getLastDayOfThisYear
     * @Description: 获取设置所属年最后时间
     */
    public static String getLastDayOfThisYear(int year) {
        LocalDateTime lastDayOfThisYear = lastDayOfThisYear(year);
        return DATETIME_FORMATTER.format(lastDayOfThisYear);
    }

    /**
     * @return String
     * @Title: getFirstDayOfThisMonth
     * @Description: 获取本月的第一天
     */
    public static String getFirstDayOfThisMonth() {
        LocalDateTime firstDayOfThisYear = get().with(TemporalAdjusters.firstDayOfMonth());
        return DATETIME_FORMATTER.format(firstDayOfThisYear);
    }

    /**
     * @return String
     * @Title: getFirstDayOfThisMonth
     * @Description: 获取本月的最末天
     */
    public static String getLastDayOfThisMonth() {
        LocalDateTime firstDayOfThisYear = get().with(TemporalAdjusters.lastDayOfMonth());
        return DATETIME_FORMATTER.format(firstDayOfThisYear);
    }

    /**
     * @param days
     * @return LocalDateTime
     * @Title: plusDays
     * @Description: 当前日期向后推多少天
     */
    public static LocalDateTime plusDays(int days) {
        return get().plusDays(days);
    }

    /**
     * 当前日期向后推多少分钟
     *
     * @param Minute
     * @return
     */
    public static LocalDateTime plusMinutes(long Minute) {
        return get().plusMinutes(Minute);
    }

    /**
     * 指定日期向后推多少分钟
     *
     * @param Minute
     * @return
     */
    public static LocalDateTime plusMinutes(LocalDateTime localDateTime, long Minute) {
        return localDateTime.plusMinutes(Minute);
    }

    /**
     * @param year
     * @param month
     * @return LocalDateTime
     * @Title: firstDayOfWeekInYearMonth
     * @Description: 获取指定年月的第一个周一
     */
    public static LocalDateTime firstDayOfWeekInYearMonth(int year, int month) {
        return get().withYear(year).withMonth(month).with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
    }

    /**
     * @return LocalDateTime
     * @Title: todayStart
     * @Description: 当天开始时间
     */
    public static LocalDateTime todayStart() {
        return LocalDateTime.of(getCurrentLocalDate(), LocalTime.MIN);
    }

    /**
     * @return LocalDateTime
     * @Title: todayEnd
     * @Description: 当天结束时间
     */
    public static LocalDateTime todayEnd() {
        return LocalDateTime.of(getCurrentLocalDate(), LocalTime.MAX);
    }

    /**
     * @return String
     * @Title: getStartDayOfWeekToString
     * @Description: 获取周第一天
     */
    public static String getStartDayOfWeekToString() {
        return formatLocalDate(getStartDayOfWeek());
    }

    public static LocalDate getStartDayOfWeek() {
        TemporalAdjuster FIRST_OF_WEEK = TemporalAdjusters.ofDateAdjuster(localDate -> localDate.minusDays(localDate
                .getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue()));
        return getCurrentLocalDate().with(FIRST_OF_WEEK);
    }

    public static long secondsToEndofDay(LocalDateTime now) {
        return Duration.between(now, todayEnd()).toMillis()/1000;
    }
    /**
     * @return String
     * @Title: getEndDayOfWeekToString
     * @Description: 获取周最后一天
     */
    public static String getEndDayOfWeekToString() {
        return formatLocalDate(getEndDayOfWeek());
    }

    public static LocalDate getEndDayOfWeek() {
        TemporalAdjuster LAST_OF_WEEK = TemporalAdjusters.ofDateAdjuster(localDate -> localDate.plusDays(
                DayOfWeek.SUNDAY.getValue() - localDate.getDayOfWeek().getValue()));
        return getCurrentLocalDate().with(LAST_OF_WEEK);
    }


    /**
     * @return LocalDateTime
     * @Title: getDateTimeOfTimestamp
     * @Description: 将long类型的timestamp转为LocalDateTime
     */
    public static LocalDateTime getDateTimeOfTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }


    /**
     * @return long
     * @Title: getTimestampOfDateTime
     * @Description: 将LocalDateTime转为long类型的timestamp
     */
    public static long getTimestampOfDateTime(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();

    }

    /**
     * 解析时间 格式yyyy-MM-dd
     *
     * @param dateStr   时间字符串
     * @return
     */
    public static LocalDateTime parseDateTime(String dateStr) {
        if (dateStr != null && !"".equals(dateStr)) {
            LocalDate localDate = LocalDate.parse(dateStr, DATE_FORMATTER);
            return localDate.atStartOfDay();
        }
        return null;
    }

    /**
     * 获取给定日期时间当日结束时间
     *
     * @param localDateTime 时间
     * @return
     */
    public static LocalDateTime dayEnd(LocalDateTime localDateTime) {
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MAX);
    }

    /**
     * 获取给定日期时间当日开始时间
     *
     * @param localDateTime 时间
     * @return
     */
    public static LocalDateTime dayStart(LocalDateTime localDateTime) {
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MIN);
    }

    /**
     * 获取昨天开始时间
     *
     * @return
     */
    public static LocalDateTime yesterdayStart() {
        return todayStart().minusDays(1);
    }

    /**
     * 获取昨天结束时间
     *
     * @return
     */
    public static LocalDateTime yesterdayEnd() {
        return todayEnd().minusDays(1);
    }
}
