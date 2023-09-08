package com.homemylove.chino.handler;

import com.homemylove.chino.entities.Message;

/**
 * 插件处理器接口，提供匹配方法
 */
public interface PluginHandler {
    boolean match(Message message);
}
