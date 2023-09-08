package com.homemylove.chino.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sender {
    private int age;
    private String area;
    private String card;
    private String level;
    @JsonProperty("nickname")
    private String nickName;
    private String role;
    private String sex;
    private String title;
    @JsonProperty("user_id")
    private Long userId;
}
