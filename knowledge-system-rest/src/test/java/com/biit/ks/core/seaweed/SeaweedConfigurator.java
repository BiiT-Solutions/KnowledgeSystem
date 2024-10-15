package com.biit.ks.core.seaweed;

import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Primary
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SeaweedConfigurator {

    public static final String UPLOADS_PATH = "/uploads-test";


    public String getUploadsPath() {
        return UPLOADS_PATH;
    }
}
