package com.homemylove.chino.handler;

import com.homemylove.chino.entities.Message;

import java.util.Objects;

public class EndWithHandler extends AbstractPluginHandler<String>{

    public EndWithHandler(String[] expectations){
        super(expectations);
    }

    @Override
    public boolean match(Message message) {
        // 获取 msg 比较后缀
        String msg = message.getMessage();
        if (msg == null || msg.isEmpty()) return false;
        for (String suffix : getExpectations()) {
            if (!Objects.equals(suffix, "") && msg.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }
}
