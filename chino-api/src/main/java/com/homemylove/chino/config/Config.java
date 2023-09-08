package com.homemylove.chino.config;

import com.homemylove.chino.proterties.ChinoControlProperties;
import com.homemylove.chino.proterties.ChinoPluginProperties;
import com.homemylove.chino.proterties.ChinoProxyProperties;
import com.homemylove.chino.proterties.ChinoListenProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties({
        ChinoListenProperties.class,
        ChinoPluginProperties.class,
        ChinoProxyProperties.class,
        ChinoControlProperties.class})
public class Config {

    @Bean("chinoRestTemplate")
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}

