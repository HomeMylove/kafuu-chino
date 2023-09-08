package com.homemylove.chino.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "listen.rabbitmq")
public class ListenProperties {

    @Getter
    @Setter
    @Configuration
    @ConfigurationProperties(prefix = "listen.rabbitmq.dev-queue")
    public static class DevQueue{
        private String name = "private";
        private Long expiration = 5000L;
        private List<Long> groupId = new ArrayList<>();
    }

    @Getter
    @Setter
    @Configuration
    @ConfigurationProperties(prefix = "listen.rabbitmq.public-queue")
    public static class PublicQueue{
        private String name = "public";
        private Long expiration = 5000L;
    }


}
