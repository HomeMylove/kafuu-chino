package com.homemylove.chino.proterties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "chino.control")
public class ChinoControlProperties {
    private List<Long> superUser = new ArrayList<>();

    private List<Long[]> frequency = new ArrayList<>();

    private Long master;
}
