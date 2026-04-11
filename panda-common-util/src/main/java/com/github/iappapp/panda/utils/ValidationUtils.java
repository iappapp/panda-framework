/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.lang.Validator
 *  cn.hutool.core.util.ReUtil
 *  org.springframework.util.StringUtils
 */
package com.github.iappapp.panda.utils;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class ValidationUtils {
    public static final String POSITIVE_INTEGER = "^[1-9]\\d*|0$";
    public static final String NEGATIVE_INTEGER = "^-[1-9]\\d*|0$";
    public static final String POSITIVE_DOUBLE = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0$";
    public static final String NEGATIVE_DOUBLE = "^(-([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*))|0?\\.0+|0$";
    public static final String DATE = "((^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._]?)(10|12|0?[13578])([-\\/\\._]?)(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._]?)(11|0?[469])([-\\/\\._]?)(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._]?)(0?2)([-\\/\\._]?)(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$)|(^([3579][26]00)([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$)|(^([1][89][0][48])([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$)|(^([2-9][0-9][0][48])([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$)|(^([1][89][2468][048])([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$)|(^([2-9][0-9][2468][048])([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$)|(^([1][89][13579][26])([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$)|(^([2-9][0-9][13579][26])([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$))";
    public static final String AGE = "^(?:[1-9][0-9]?|1[01][0-9]|120)$";
    public static final String WORD_OR_NUMBER = "^[A-Za-z0-9]+";
    public static final String SPECIAL_STR = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~\uff01@#\uffe5%\u2026\u2026&*\uff08\uff09\u2014\u2014+|{}\u3010\u3011\u2018\uff1b\uff1a\u201d\u201c'\u3002\uff0c\u3001\uff1f]";
    public static final String PARAMETER_SPECIAL_STR = "[\\\\`~!@#$%^&*()\"+=|{}':;',\\[\\]<>/?~@#\uffe5%\u2026\u2026&*\uff08\uff09\u2014\u2014+|{}\u3010\u3011\u2018\u2019]";

    public static boolean isNegativeInteger(CharSequence charSequence) {
        return Validator.isMactchRegex(NEGATIVE_INTEGER, charSequence);
    }

    public static boolean isPositiveInteger(CharSequence charSequence) {
        return Validator.isMactchRegex(POSITIVE_INTEGER, charSequence);
    }

    public static boolean isNegativeDouble(CharSequence charSequence) {
        return Validator.isMactchRegex(NEGATIVE_DOUBLE, charSequence);
    }

    public static boolean isPositiveDouble(CharSequence charSequence) {
        return Validator.isMactchRegex(POSITIVE_DOUBLE, charSequence);
    }

    public static boolean isDate(CharSequence charSequence) {
        return Validator.isMactchRegex(DATE, charSequence);
    }

    public static boolean isAge(CharSequence charSequence) {
        return Validator.isMactchRegex(AGE, charSequence);
    }

    public static boolean isWordOrNumber(CharSequence charSequence) {
        return Validator.isMactchRegex(WORD_OR_NUMBER, charSequence);
    }

    public static String filterSpecialStr(String str) {
        Pattern p = Pattern.compile(SPECIAL_STR);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static boolean hasSpecialStr(String str) {
        return !StrUtil.isEmpty(ReUtil.getGroup0(PARAMETER_SPECIAL_STR, str));
    }
}

