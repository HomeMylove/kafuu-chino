package com.homemylove.chino.service;


public interface SendService {

    /**
     * 发送消息到消息队列
     * @param queueName 队列名称
     * @param msg 消息内容
     */
    void sendMessage(String queueName,String msg,Long expiration);

}
