package com.homemylove.chino.entities.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MessageInfo {
    @JsonProperty("message_id")
    private Long messageId;
}
