package com.homemylove.chino.api;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Data
public class MsgInfo {
    private String message;
    private Date time;
}
