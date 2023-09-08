package com.homemylove.chino.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homemylove.chino.entities.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class PluginListener {

    @Resource
    private PluginDispatcher dispatcher;


    @RabbitListener(queues = "${chino.listen.rabbitmq.name:public}")
    public void listen(String request) {
        log.info("收到消息:{}", request);
        log.info("{}",int.class.getName());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Message message = objectMapper.readValue(request, Message.class);
            message.setMessage(message.getMessage() == null ? null : message.getMessage().trim());
            dispatcher.dispatcher(message);
        } catch (Exception e) {
            log.error("error" + e);
        }

    }

}
