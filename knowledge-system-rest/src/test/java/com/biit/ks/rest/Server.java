package com.biit.ks.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ConfigurationPropertiesScan({"com.biit.ks"})
@EnableJpaRepositories({"com.biit.ks.persistence.repositories"})
@EntityScan({"com.biit.ks.persistence.entities"})
@ComponentScan({"com.biit.ks", "com.biit.usermanager.client", "com.biit.server.client"})
public class Server {
    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }
}
