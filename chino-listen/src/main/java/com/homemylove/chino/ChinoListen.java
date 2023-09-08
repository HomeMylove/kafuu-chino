package com.homemylove.chino;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@EnableRabbit
@SpringBootApplication
public class ChinoListen {

    public static void main(String[] args) {
        SpringApplication.run(ChinoListen.class, args);
    }

    @Bean("listenRestTemplate")
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
