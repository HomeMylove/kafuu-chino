package com.homemylove.chino.handler;

import com.homemylove.chino.entities.Message;

/**
 * Anyway 处理器
 * 必定通过
 */
public class AnywayHandler extends AbstractPluginHandler<Object>{

    /**
     * 只要传入就通过
     * @param message 内容
     * @return true
     */
    @Override
    public boolean match(Message message) {
        return true;
    }
}
