package com.azkj.sws.library.logger;

import android.support.annotation.NonNull;

/**
 * Created by Administrator on 2017/8/22.
 */

public class LogTag {
    private String tag;

    private LogTag(@NonNull String tag) {
        this.tag = tag;
    }

    public static LogTag mk(@NonNull String tag) {
        return new LogTag(tag);
    }

    public String gTag() {
        return tag;
    }
}
