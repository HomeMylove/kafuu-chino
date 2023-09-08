package com.homemylove.chino.config;

import com.homemylove.chino.proterties.ChinoListenProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class RabbitConfig {
    @Resource
    private ChinoListenProperties listenProperties;

    @Bean
    public Queue requestQueue(){
        return new Queue(listenProperties.getName(),true);
    }

}