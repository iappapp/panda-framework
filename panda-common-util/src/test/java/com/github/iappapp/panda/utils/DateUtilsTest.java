package com.github.iappapp.panda.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * @author iappapp
 * @date 2020/9/7 16:57
 */
@Slf4j
public class DateUtilsTest {

    @Test
    public void formatLDateTime() {
        String date = "20200907170450";
        Date parseDate = DateUtils.parseDate(date, DateUtils.LDATETIME_FORMAT);
        String str = DateUtils.formatLDateTime(parseDate);
        Assert.assertEquals(str, date);
    }

    @Test
    public void formatShortYearDateTime() {
        String date = "20200907170450";
        Date parseDate = DateUtils.parseDate(date, DateUtils.LDATETIME_FORMAT);
        String str = DateUtils.formatShortYearDateTime(parseDate);
        Assert.assertEquals("200907170450", str);
    }
}
