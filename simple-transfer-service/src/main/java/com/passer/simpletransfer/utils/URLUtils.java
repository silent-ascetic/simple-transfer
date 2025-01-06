package com.passer.simpletransfer.utils;

import java.nio.charset.Charset;

public class URLUtils {
    public static String getDataUri(String mimeType, String encoding, String data) {
        return getDataUri(mimeType, null, encoding, data);
    }

    public static String getDataUri(String mimeType, Charset charset, String encoding, String data) {
        StringBuilder builder = StringUtils.builder("data:");
        if (StringUtils.isNotBlank(mimeType)) {
            builder.append(mimeType);
        }

        if (null != charset) {
            builder.append(";charset=").append(charset.name());
        }

        if (StringUtils.isNotBlank(encoding)) {
            builder.append(';').append(encoding);
        }

        builder.append(',').append(data);
        return builder.toString();
    }
}
