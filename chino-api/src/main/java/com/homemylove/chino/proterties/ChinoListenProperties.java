package com.homemylove.chino.proterties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;

@Data
@ConfigurationProperties(prefix = "chino.listen.rabbitmq",ignoreInvalidFields = true,ignoreUnknownFields = false)
public class ChinoListenProperties {
    private String name = "public";
}
