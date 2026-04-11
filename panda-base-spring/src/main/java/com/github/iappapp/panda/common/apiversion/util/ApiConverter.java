package com.github.iappapp.panda.common.apiversion.util;

import com.github.iappapp.panda.common.apiversion.config.ApiItem;
import org.apache.commons.lang3.StringUtils;

public class ApiConverter {

    public static final String DEFAULT_VERSION = "1.0.0";

    public static ApiItem convert(String version) {
        ApiItem apiItem = new ApiItem();
        if (StringUtils.isEmpty(version)) {
            return apiItem;
        }

        String[] versionArr = version.split("\\.");
        apiItem.setHigh(Integer.parseInt(versionArr[0]));

        if (versionArr.length > 1) {
            apiItem.setMid(Integer.parseInt(versionArr[1]));
        }

        if (versionArr.length > 2) {
            apiItem.setLow(Integer.parseInt(versionArr[2]));
        }
        return apiItem;
    }
}
