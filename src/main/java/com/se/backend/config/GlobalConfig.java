package com.se.backend.config;

import org.springframework.stereotype.Component;

@Component
public class GlobalConfig {
    static final String STATIC_URL = "https://file.wmzspace.space/";

    public static String getStaticUrl(String relativeUrl) {
        return STATIC_URL.concat(relativeUrl);
    }
}
