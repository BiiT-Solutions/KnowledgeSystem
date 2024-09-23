package com.biit.ks.rest;

import com.biit.server.security.userguard.UserGuardDatabaseConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = {"com.biit.ks", "com.biit.server", "com.biit.usermanager.client", "com.biit.messagebird.client"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {UserGuardDatabaseConfigurator.class})})
@ConfigurationPropertiesScan({"com.biit.ks.rest", "com.biit.server.security.userguard"})
public class KnowledgeSystemServer {
    public static void main(String[] args) {
        SpringApplication.run(KnowledgeSystemServer.class, args);
    }
}
