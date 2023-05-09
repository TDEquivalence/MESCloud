package com.tde.mescloud.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;

public class ObjectMapperSettings {

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
