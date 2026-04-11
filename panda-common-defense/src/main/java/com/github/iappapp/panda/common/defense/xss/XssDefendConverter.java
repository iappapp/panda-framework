/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.stereotype.Component
 *  org.springframework.util.StringUtils
 */
package com.github.iappapp.panda.common.defense.xss;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class XssDefendConverter
implements Converter<String, String> {
    private static final String[] BAD_CHARS = new String[]{">", "<"};
    private static final String[] GOOD_CHARS = new String[]{"&gt;", "&lt;"};

    public String convert(String param) {
        return StringUtils.isEmpty((Object)param) ? param : param.replaceAll(BAD_CHARS[0], GOOD_CHARS[0]).replaceAll(BAD_CHARS[1], GOOD_CHARS[1]);
    }
}

