package com.tde.mescloud.utility;

<<<<<<< HEAD
import com.fasterxml.jackson.databind.ObjectMapper;
=======
>>>>>>> aa278757c05559b51687e8f330e3f47fab062a82
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanFactory {

    @Bean
<<<<<<< HEAD
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
=======
>>>>>>> aa278757c05559b51687e8f330e3f47fab062a82
    public ModelMapper getModelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setPreferNestedProperties(false);
        return mapper;
    }
}
