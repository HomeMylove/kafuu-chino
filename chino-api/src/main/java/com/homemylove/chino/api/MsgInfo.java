package com.homemylove.chino.api;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 保存发出的消息
 */
@NoArgsConstructor
@Data
public class MsgInfo {
    private Long groupId;
    private String message;
    private Date time;
}
