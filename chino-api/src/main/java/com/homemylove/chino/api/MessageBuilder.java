package com.homemylove.chino.api;

import java.net.URL;

/**
 * 创造 一条 message
 */
public class MessageBuilder {

    private final StringBuilder sb = new StringBuilder();

    public MessageBuilder(){}

    public MessageBuilder(String str){
        sb.append(str);
    }

    public MessageBuilder message(boolean condition,String message){
        if(condition) sb.append(message);
        return this;
    }

    public MessageBuilder message(String message){
        sb.append(message);
        return this;
    }

    public MessageBuilder image(String file){
        sb.append("[CQ:image,file=file://").append(file).append("]");
        return this;
    }

    public MessageBuilder image(URL url){
        sb.append("[CQ:image,file=").append(url.toString()).append("]");
        return this;
    }

    public MessageBuilder at(Long atId){
        sb.append("[CQ:at,qq=").append(atId).append("]");
        return this;
    }

    public String build(){
        return sb.toString();
    }
}
