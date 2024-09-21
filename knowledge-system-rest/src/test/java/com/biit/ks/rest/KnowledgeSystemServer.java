package com.biit.ks.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({"com.biit.ks", "com.biit.server", "com.biit.usermanager.client", "com.biit.messagebird.client"})
@ConfigurationPropertiesScan({"com.biit.ks.rest", "com.biit.server.security.userguard"})
@EntityScan({"com.biit.ks.persistence.entities", "com.biit.server.security.userguard"})
public class KnowledgeSystemServer {
    public static void main(String[] args) {
        SpringApplication.run(KnowledgeSystemServer.class, args);
    }
}
