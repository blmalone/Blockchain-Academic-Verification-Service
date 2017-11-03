package com.unilog.app.utils;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the beans and settings required for Dozer Object to Object mapping.
 */
@Configuration
public class DozerMapperConfig {

    @Bean
    Mapper mapper() {
        return new DozerBeanMapper();
    }
}
