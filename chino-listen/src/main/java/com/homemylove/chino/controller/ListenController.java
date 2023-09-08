package com.homemylove.chino.controller;


import com.homemylove.chino.entities.Message;
import com.homemylove.chino.service.HandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 监听 5701 端口，上传到 mq
 */
@RestController
@Slf4j
public class ListenController {

    @Resource
    private HandleService handleService;

    @PostMapping("/")
    public void send(HttpServletRequest request){
        try {
            Message message = handleService.generateMessage(request);
            log.info("message:{}",message);
            // 空消息
            if(message == null) return;
            handleService.shunt(message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


}
