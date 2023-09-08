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

    private static final String GROUP_ID = "group_id";
    private static final String USER_ID = "user_id";
    private static final String MESSAGE = "message";
    private static final String AUTO_ESCAPE = "auto_escape";
    private static final String END_POINT = "endPoint";

    private static final HttpHeaders headers = new HttpHeaders();

    static {
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    @Qualifier("chinoRestTemplate")
    private RestTemplate restTemplate;

    @Resource
    private ChinoProxyProperties proxyProperties;

    /**
     * 获取目标 url
     *
     * @return 目标 url
     */
    private String getUrl() {
        return proxyProperties.getHost() + ":" + proxyProperties.getPort() + proxyProperties.getPath();
    }

    /**
     * 发送一条群聊消息
     *
     * @param groupId    QQ群号
     * @param message    message 内容
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 )
     * @return 结果
     */
    public Result<MessageInfo> sendGroupMsg(Long groupId, String message, boolean autoEscape) {
        HashMap<String, Object> data = new HashMap<>();
        data.put(END_POINT, "send_group_msg");
        data.put(MESSAGE, message);
        data.put(GROUP_ID, groupId);
        data.put(AUTO_ESCAPE, autoEscape);

        // 创建HttpEntity对象，将Map作为请求体数据并设置请求头
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(data, headers);

        // 发送POST请求，并获取响应
        String string = restTemplate.postForObject(getUrl(), requestEntity, String.class);
        return transfer(string, MessageInfo.class);
    }

    public Result<MessageInfo> sendGroupMsg(Long groupId, String message) {
        return sendGroupMsg(groupId, message, false);
    }

    public Result<MessageInfo> sendGroupImage(Long groupId, URL url) {
        return sendGroupMsg(groupId,new MessageBuilder().image(url).build());
    }

    public Result<MessageInfo> sendGroupImage(Long groupId, String file) {
        return sendGroupMsg(groupId,new MessageBuilder().image(file).build());
    }

    /**
     * 获取群成员信息
     * @param groupId QQ群号
     * @param userId QQ号
     * @return GroupMemberInfo 信息
     */
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
