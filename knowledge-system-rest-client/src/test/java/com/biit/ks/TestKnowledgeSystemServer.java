package com.biit.ks;


import com.biit.server.security.userguard.UserGuardDatabaseConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ComponentScan(basePackages = {"com.biit.ks", "com.biit.server", "com.biit.usermanager.client", "com.biit.messagebird.client", "com.biit.server.client"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {UserGuardDatabaseConfigurator.class})})
@ConfigurationPropertiesScan({"com.biit.ks"})
public class TestKnowledgeSystemServer {

    public static void main(String[] args) {
        SpringApplication.run(TestKnowledgeSystemServer.class, args);
    }
}
