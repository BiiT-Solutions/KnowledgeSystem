package com.biit.ks.rest;

import com.biit.ks.logger.KnowledgeSystemLogger;
import com.biit.server.security.userguard.UserGuardDatabaseConfigurator;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.DispatcherServlet;

//Avoid Swagger redirecting https to http
@OpenAPIDefinition(servers = {@Server(url = "${server.servlet.context-path}", description = "Default Server URL")})
@SpringBootApplication
@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource(value = "file:${EXTERNAL_CONFIG_FILE}", ignoreResourceNotFound = true)
})
@ComponentScan(basePackages = {"com.biit.ks", "com.biit.server.security", "com.biit.server", "com.biit.messagebird.client", "com.biit.usermanager.client"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {UserGuardDatabaseConfigurator.class})})
@ConfigurationPropertiesScan({"com.biit.ks.rest"})
public class KnowledgeSystemServer {
    private static final int POOL_SIZE = 20;
    private static final int MAX_POOL_SIZE = 100;

    public static void main(String[] args) {
        SpringApplication.run(KnowledgeSystemServer.class, args);
    }

    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }


    @Bean("threadPoolExecutor")
    public TaskExecutor getAsyncExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("Rest_Async-");
        return executor;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ApplicationListener<ContextRefreshedEvent> startupLoggingListener() {
        return event -> KnowledgeSystemLogger.info(KnowledgeSystemServer.class, "### Server started ###");
    }

    @Bean
    public UserGuardDatabaseConfigurator userGuardDatabaseConfigurator() {
        try {
            return new UserGuardDatabaseConfigurator();
        } catch (UnsupportedClassVersionError e) {
            return null;
        }
    }
}
