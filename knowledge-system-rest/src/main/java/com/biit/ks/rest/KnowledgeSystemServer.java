package com.biit.ks.rest;

/*-
 * #%L
 * Knowledge System (Rest)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.ks.logger.KnowledgeSystemLogger;
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
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.DispatcherServlet;


@EnableScheduling
//Avoid Swagger redirecting https to http
@OpenAPIDefinition(servers = {@Server(url = "${server.servlet.context-path}", description = "Default Server URL")})
@SpringBootApplication
@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource(value = "file:${EXTERNAL_CONFIG_FILE}", ignoreResourceNotFound = true)
})
@ComponentScan(basePackages = {"com.biit.ks", "com.biit.server.security", "com.biit.server", "com.biit.messagebird.client", "com.biit.usermanager.client",
        "com.biit.kafka"})
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
}
