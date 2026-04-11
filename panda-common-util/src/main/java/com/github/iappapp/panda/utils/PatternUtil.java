package com.github.iappapp.panda.utils;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liush2
 * @date 2019/8/30 11:21
 * @remarks 正则工具
 */
public class PatternUtil {

    public static final Pattern PATTERN_NATURAL = Pattern.compile("^(0|[1-9]+[0-9]*)$");

    public static final Pattern PATTERN_POSITIVE = Pattern.compile("^([1-9]+[0-9]*)$");

    /**
     * 从json中使用正则表达式匹配某一项数据
     *
     * @param pattern
     * @param str
     * @return
     */
    public static String getPatternString(String pattern, String str) {
        String result = null;
        Pattern custName = Pattern.compile("\"" + pattern + "\":\"(.*?)\"");

        Matcher matcher = custName.matcher(str);

        if ((matcher != null) && (matcher.find())) {
            result = matcher.group(1);
        }

        return result;
    }


    public static boolean getFlowShowPatter(String ratableResourceID) {


        Pattern pattern = Pattern.compile("33" + "(.*?)" + "100");

        Matcher matcher = pattern.matcher(ratableResourceID);

        if ((matcher != null) && (matcher.find())) {
            return true;
        }

        return false;
    }

    public static boolean isValidRatableId(String ratableResourceID) {

        if (StringUtils.isEmpty(ratableResourceID)) {
            return false;
        }

        if (ratableResourceID.length() != 6) {
            return false;
        }

        if (ratableResourceID.indexOf("0", 4) != 4) {
            return false;
        }
        return true;
    }

    public static boolean getIsLimitPattern(String ratableResourceID) {


        Pattern pattern = Pattern.compile("3" + "(...*?)" + "01");

        Matcher matcher = pattern.matcher(ratableResourceID);

        if ((matcher != null) && (matcher.find())) {
            return true;
        }

        return false;
    }

    public static int getIsDirectionFlow(String ratableResourceID) {

        if (StringUtils.isEmpty(ratableResourceID)) {
            return 0;
        }

        if (ratableResourceID.indexOf("2", 3) != 3) {
            return 0;
        }
        return 1;
    }

    public static int isUnlimitFlow(String resourceId) {
        if (StringUtils.isEmpty(resourceId)) {
            return 0;
        }

        return PatternUtil.getIsLimitPattern(resourceId) ? 1 : 0;
    }


    public static boolean strIsNumber(String str) {
        boolean flag = false;
        if (str != null && !str.equals("")) {
            Pattern isNumber = Pattern.compile("^\\d+$|-\\d+$");
            Pattern isDecimal = Pattern.compile("\\d+\\.\\d+$|-\\d+\\.\\d+$");
            if (isNumber.matcher(str).matches() || isDecimal.matcher(str).matches()) {
                flag = true;
            }
        }
        return flag;
    }


    /**
     * 获取脱敏手机号
     *
     * @param mobile
     * @return
     */
    public static String getShortMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return null;
        } else {
            return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
    }

    public static boolean isNaturalNum(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return PATTERN_NATURAL.matcher(str).find();
    }

    public static boolean isPositiveNum(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return PATTERN_POSITIVE.matcher(str).find();
    }
}
