package com.homemylove.chino.proterties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "chino.plugin",ignoreInvalidFields = true,ignoreUnknownFields = false)
public class ChinoPluginProperties {

    @Data
    @Configuration
    @AllArgsConstructor
    @NoArgsConstructor
    @ConfigurationProperties(value = "chino.plugin.priority",ignoreInvalidFields = true,ignoreUnknownFields = false)
    public static class Priority{
        private int startWith = 8;
        private int endWith = 2;
        private int contains = 4;
        private int regex = 16;
        private int equals = 32;
    }


}
