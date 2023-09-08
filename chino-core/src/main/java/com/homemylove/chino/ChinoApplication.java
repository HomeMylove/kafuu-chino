package com.homemylove.chino;

import com.homemylove.chino.dispatcher.PluginDispatcher;
import com.homemylove.chino.dispatcher.PluginListener;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.homemylove.chino.mapper")
public class ChinoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChinoApplication.class);
    }

    @Bean
    public PluginListener listenService(){

        return new PluginListener();
    }
}
