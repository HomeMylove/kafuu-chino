package com.homemylove.chino.adapter;

import com.homemylove.chino.entities.Message;

/**
 * 支持，检验一个 message 能不能通过测试
 */
public interface Supports{
    boolean supports(Message message);
}
