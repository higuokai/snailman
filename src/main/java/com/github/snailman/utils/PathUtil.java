package com.github.snailman.utils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PathUtil {

    private static final String SPLIT = "/";

    @NotNull
    public static String formatPath(@Nullable Object path) {
        if (path == null) {
            return SPLIT;
        }
        String currPath;
        if (path instanceof String) {
            currPath = (String) path;
        } else {
            currPath = path.toString();
        }
        if (currPath.startsWith(SPLIT)) {
            return currPath;
        }
        return SPLIT + currPath;
    }

    @NotNull
    public static String buildUrl(@NotNull String protocol, @Nullable Integer port, @Nullable String contextPath, String path) {
        StringBuilder url = new StringBuilder(protocol + "://");
        url.append("localhost");

        if (port != null) {
            url.append(":").append(port);
        } else {
            url.append(":").append("8080");
        }

        if (StringUtils.isNotBlank(contextPath)) {
            contextPath = Joiner.on("/").join(Splitter.on("/").omitEmptyStrings().split(contextPath));
            url.append(SPLIT).append(contextPath);
        }

        if (!path.startsWith(SPLIT)) {
            url.append(SPLIT);
        }

        url.append(path);
        return url.toString();
    }

}
