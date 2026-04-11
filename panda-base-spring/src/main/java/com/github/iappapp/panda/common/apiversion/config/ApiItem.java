package com.github.iappapp.panda.common.apiversion.config;

import com.github.iappapp.panda.common.apiversion.util.ApiConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApiItem implements Comparable<ApiItem> {
    private int high = 1;

    private int mid = 0;

    private int low = 0;

    public static final ApiItem API_ITEM_DEFAULT =
            ApiConverter.convert(ApiConverter.DEFAULT_VERSION);

    public ApiItem() {
    }

    public ApiItem(String version) {
        String[] apiVersion = version.split(",");
        if (apiVersion.length != 3) {
            // ignore
        } else {
            this.high = Integer.parseInt(apiVersion[0]);
            this.mid = Integer.parseInt(apiVersion[1]);
            this.low = Integer.parseInt(apiVersion[2]);
        }
    }

    @Override
    public int compareTo(ApiItem other) {
        if (this.high > other.high) {
            return 1;
        } else if (this.high < other.high) {
            return -1;
        }

        if (this.mid > other.mid) {
            return 1;
        } else if (this.mid < other.mid) {
            return -1;
        }
        if (this.low > other.low) {
            return 1;
        } else if (this.low < other.low) {
            return -1;
        }
        return 0;
    }
}
