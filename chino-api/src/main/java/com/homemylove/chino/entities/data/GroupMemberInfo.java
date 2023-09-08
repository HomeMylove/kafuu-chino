package com.homemylove.chino.entities.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupMemberInfo {
    private int age;
    private String area;
    private String card;
    @JsonProperty("card_changeable")
    private boolean cardChangeAble;
    @JsonProperty("group_id")
    private Long groupId;
    @JsonProperty("join_time")
    private Date joinTime;
    @JsonProperty("last_sent_time")
    private Date lastSentTime;
    private String level;
    private String nickname;
    private String role;
    private String sex;
    private String title;
    private boolean unfriendly;
    @JsonProperty("user_id")
    private Long userId;
}
