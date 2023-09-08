package com.homemylove.chino.service;

import com.homemylove.chino.entities.Message;
import javax.servlet.http.HttpServletRequest;

public interface HandleService {

    /**
     * 获取 HttpServletRequest 中的 Body，转成 Message 对象
     * @param request HttpServletRequest
     * @return Message 对象
     */
    Message generateMessage(HttpServletRequest request);

    /**
     * 消息分流，根据配置将消息分发到不同到队列
     * @param message Message 对象
     */
    void shunt(Message message);
}
