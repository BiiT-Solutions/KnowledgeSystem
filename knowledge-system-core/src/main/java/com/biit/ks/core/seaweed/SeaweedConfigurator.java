package com.biit.ks.core.seaweed;

import org.springframework.stereotype.Component;

@Component
public class SeaweedConfigurator {

    public static final String UPLOADS_PATH = "/uploads";


    public String getUploadsPath() {
        return UPLOADS_PATH;
    }
}
