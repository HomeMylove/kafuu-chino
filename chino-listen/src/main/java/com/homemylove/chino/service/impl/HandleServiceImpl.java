package com.homemylove.chino.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homemylove.chino.entities.Message;
import com.homemylove.chino.properties.ListenProperties;
import com.homemylove.chino.service.HandleService;
import com.homemylove.chino.service.SendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HandleServiceImpl implements HandleService {

    @Resource
    private ListenProperties.DevQueue devQueue;

    @Resource
    private ListenProperties.PublicQueue publicQueue;

    @Resource
    private SendService sendService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Message generateMessage(HttpServletRequest request) {
        try {
            BufferedReader reader = request.getReader();
            String requestBody = reader.lines().collect(Collectors.joining());
            if (requestBody.contains("meta_event_type")) return null;
            return objectMapper.readValue(requestBody, Message.class);
        } catch (Exception e) {
            throw new RuntimeException("转化为 Message 过程中出现错误:"+e.getMessage());
        }
    }

    @Override
    public void shunt(Message message) {
        // Real GroupId
        try {
            Long groupId = message.getGroupId();
            // Dev Groups
            List<Long> ids = devQueue.getGroupId();

            String msgStr = objectMapper.writeValueAsString(message);

            log.info("group_id:{}",groupId);
            log.info("ids:" + ids.toString());


            // GroupId in Dev Groups
            if(ids.contains(groupId)){
                sendService.sendMessage(devQueue.getName(),msgStr,devQueue.getExpiration());
                log.info("private");
            }else {
                log.info("public");
                sendService.sendMessage(publicQueue.getName(),msgStr,publicQueue.getExpiration());
            }
            log.info("发送消息到:{},内容:{}",groupId,msgStr);
        }catch (Exception e){
            throw new RuntimeException("上传在 rabbitmq 过程中出现错误:" + e.getMessage());
        }
    }


}
