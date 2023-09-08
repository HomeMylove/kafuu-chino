package com.homemylove.chino.config;

import com.homemylove.chino.properties.ListenProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ListenProperties.class})
public class Config {
}
