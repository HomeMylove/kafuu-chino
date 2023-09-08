package com.homemylove.chino.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class ProxyController {

    @Resource
    private RestTemplate restTemplate;

    @Value("${proxy.host}")
    private String HOST;

    @Value("${proxy.port}")
    private String PORT;

    @GetMapping("/chino")
    public String chino(){
        return "hello World";
    }

    /**
     * 只负责转发
     * @return
     */
    @PostMapping("/chino")
    public String proxy(@RequestBody Map<String,Object> map){
        log.info("MAP:{}",map);
        String endPoint =(String) map.get("endPoint");

        String url = HOST + ":" + PORT +
                (endPoint.indexOf("/") == 0 ? endPoint : "/" + endPoint);

        HashMap<String, Object> data = new HashMap<>();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if(!"endPoint".equals(entry.getKey())){
                data.put(entry.getKey(),entry.getValue());
            }
        }

        // 设置请求头，指定Content-Type为application/json
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 创建HttpEntity对象，将Map作为请求体数据并设置请求头
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(data, headers);

        // 发送POST请求，并获取响应
        return restTemplate.postForObject(url, requestEntity, String.class);
    }
}
