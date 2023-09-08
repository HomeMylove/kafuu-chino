package com.homemylove.chino.config;

import com.homemylove.chino.properties.ListenProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class RabbitConfig {

    @Resource
    private ListenProperties.DevQueue devQueue;

    @Resource
    private ListenProperties.PublicQueue publicQueue;

    @Bean("devListenQueue")
    public Queue devListenQueue(){
        return new Queue(devQueue.getName(),true);
    }

    @Bean("publicListenQueue")
    public Queue publicListenQueue(){
        return new Queue(publicQueue.getName(),true);
    }

}