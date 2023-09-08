package com.homemylove.chino.service.impl;

import com.homemylove.chino.service.SendService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SendServiceImpl implements SendService {

    @Resource
    @Qualifier("listenRestTemplate")
    private RabbitTemplate rabbitTemplate;

    @Override
    public void sendMessage(String queueName, String msg,Long expiration) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setExpiration(expiration.toString());
        Message message = new Message(msg.getBytes(), messageProperties);
        rabbitTemplate.convertAndSend(queueName,message);
    }
}
