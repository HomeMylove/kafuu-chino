package com.homemylove.chino.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homemylove.chino.entities.Result;
import com.homemylove.chino.entities.data.GroupMemberInfo;
import com.homemylove.chino.entities.data.MessageInfo;
import com.homemylove.chino.proterties.ChinoProxyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ApiChan {

    @Resource
    @Qualifier("chinoRestTemplate")
    private RestTemplate restTemplate;
    private static final String GROUP_ID = "group_id";
    private static final String USER_ID = "user_id";

    private static final String MESSAGE = "message";
    private static final String AUTO_ESCAPE = "auto_escape";
    private static final String END_POINT = "endPoint";

    @Resource
    private ObjectMapper objectMapper;

    private static enum IMAGE_TYPE {
        FILE,
        HTTP
    }

    @Resource
    private ChinoProxyProperties proxyProperties;

    private String getUrl() {
        return proxyProperties.getHost() + ":" + proxyProperties.getPort() + proxyProperties.getPath();
    }

    public Result<MessageInfo> sendGroupMsg(Long groupId, String message, boolean autoEscape) {
        HashMap<String, Object> data = new HashMap<>();
        data.put(END_POINT, "send_group_msg");
        data.put(MESSAGE, message);
        data.put(GROUP_ID, groupId);
        data.put(AUTO_ESCAPE, autoEscape);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 创建HttpEntity对象，将Map作为请求体数据并设置请求头
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(data, headers);

        System.out.println("url" + getUrl());
        System.out.println("message" + message);
        // 发送POST请求，并获取响应

        String string = restTemplate.postForObject(getUrl(), requestEntity, String.class);
        return transfer(string,MessageInfo.class);
    }

    public Result<MessageInfo> sendGroupMsg(Long groupId, String message) {
        return sendGroupMsg(groupId, message, false);
    }

    public Result<MessageInfo> sendGroupImage(Long groupId, URL url) {
        return sendGroupImage(groupId, IMAGE_TYPE.HTTP, url.toString());
    }

    public Result<MessageInfo> sendGroupImage(Long groupId, String file) {
        return sendGroupImage(groupId, IMAGE_TYPE.FILE, file);
    }

    private Result<MessageInfo> sendGroupImage(Long groupId, IMAGE_TYPE type, String path) {
        StringBuilder sb = new StringBuilder("[CQ:image,file=");
        if (IMAGE_TYPE.FILE.equals(type)) {
            sb.append("file://");
        }
        sb.append(path).append("]");
        return sendGroupMsg(groupId, sb.toString());
    }

    public Result<GroupMemberInfo> getGroupMemberInfo(Long groupId, Long userId) {
        HashMap<String, Object> data = new HashMap<>();
        data.put(END_POINT, "get_group_member_info");
        data.put(GROUP_ID, groupId);
        data.put(USER_ID, userId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 创建HttpEntity对象，将Map作为请求体数据并设置请求头
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(data, headers);
        // 发送POST请求，并获取响应
        String string = restTemplate.postForObject(getUrl(), requestEntity, String.class);
        return transfer(string, GroupMemberInfo.class);
    }

    private <T> Result<T> transfer(String resultStr, Class<T> clazz) {
        try {
            Result<T> result = objectMapper.readValue(resultStr, Result.class);
            String string = objectMapper.writeValueAsString(result.getData());
            T t = objectMapper.readValue(string, clazz);
            result.setData(t);
            return result;
        } catch (Exception e) {
            log.error("结果转换错误:{}", e.getMessage());
            return null;
        }
    }
}
