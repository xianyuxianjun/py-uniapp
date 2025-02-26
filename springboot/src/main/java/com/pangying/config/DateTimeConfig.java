package com.pangying.config;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.LocalDateTime;

@Configuration
public class DateTimeConfig {
    private static final String[] DATE_TIME_PATTERNS = {
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd HH:mm:ss"
    };

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.simpleDateFormat(DATE_TIME_PATTERNS[0]);
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(
                new DateTimeFormatterBuilder()
                    .appendPattern("[yyyy-MM-dd'T'HH:mm:ss][yyyy-MM-dd'T'HH:mm:ss.SSS][yyyy-MM-dd HH:mm:ss]")
                    .toFormatter()
            ));
        };
    }
} 