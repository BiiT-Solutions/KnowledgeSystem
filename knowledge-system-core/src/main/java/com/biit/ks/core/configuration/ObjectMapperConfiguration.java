package com.biit.ks.core.configuration;

/*-
 * #%L
 * Knowledge System (Core)
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.format.DateTimeFormatter;


@Configuration
public class ObjectMapperConfiguration {
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final LocalDateTimeSerializer LOCAL_DATETIME_SERIALIZER = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT));


    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        final JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LOCAL_DATETIME_SERIALIZER);
        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(module);
    }

    @Bean
    public JavaTimeModule javaTimeModule() {
        final JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LOCAL_DATETIME_SERIALIZER);
        return module;
    }

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder().serializers(LOCAL_DATETIME_SERIALIZER)
                .serializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Bean
    public JacksonJsonpMapper jacksonJsonMapper() {
        return new JacksonJsonpMapper(new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.ALWAYS));
    }


}
