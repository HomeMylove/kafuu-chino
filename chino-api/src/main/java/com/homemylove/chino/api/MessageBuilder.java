package com.homemylove.chino.api;

import java.net.URL;

/**
 * 提供更好的生成 message string 的方法
 */
public class MessageBuilder {

    private final StringBuilder sb = new StringBuilder();

    public MessageBuilder(){}

    public MessageBuilder(String start){
        sb.append(start);
    }

    /**
     * 根据 condition 判断是否传入
     * @param condition 条件，为 true 时传入
     * @param message message string
     * @return MessageBuilder
     */
    public MessageBuilder message(boolean condition,String message){
        if(condition) this.message(message);
        return this;
    }

    public MessageBuilder message(String message){
        sb.append(message);
        return this;
    }

    /**
     * 传入本地图片路径
     * @param file 本地图片路径
     * @return MessageBuilder
     */
    public MessageBuilder image(String file){
        sb.append("[CQ:image,file=file://").append(file).append("]");
        return this;
    }

    /**
     * 传入网络图片路径
     * @param url URL 对象
     * @return MessageBuilder
     */
    public MessageBuilder image(URL url){
        sb.append("[CQ:image,file=").append(url.toString()).append("]");
        return this;
    }

    /**
     * 艾特某人
     * @param atId QQ号
     * @return MessageBuilder
     */
    public MessageBuilder at(Long atId){
        sb.append("[CQ:at,qq=").append(atId).append("]");
        return this;
    }

    /**
     * 艾特某人
     * @param atId QQ号
     * @return MessageBuilder
     */
    public MessageBuilder at(String atId){
        sb.append("[CQ:at,qq=").append(atId).append("]");
        return this;
    }

    public String build(){
        return sb.toString();
    }
}
