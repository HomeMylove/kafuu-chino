package com.homemylove.chino.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
    @JsonProperty("post_type")
    private String postType;
    @JsonProperty("message_type")
    private String messageType;
    private Date time;
    @JsonProperty("self_id")
    private Long selfId;
    @JsonProperty("sub_type")
    private String subType;
    private String message;
    private Sender sender;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("message_id")
    private Long messageId;
    @JsonProperty("group_id")
    private Long groupId;
}
