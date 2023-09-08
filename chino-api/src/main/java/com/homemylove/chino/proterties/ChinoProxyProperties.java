package com.homemylove.chino.proterties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "chino.proxy")
public class ChinoProxyProperties {

    private String host = "http://124.71.159.79";
    private int port = 24466;
    private String path = "/chino";

}
