package com.homemylove.chino.handler;

import com.homemylove.chino.entities.Message;

import java.util.Objects;

public class ContainsHandler extends AbstractPluginHandler<String>{

    public ContainsHandler(String[] expectations){
        super(expectations);
    }

    @Override
    public boolean match(Message message) {
        // 获取 msg
        String msg = message.getMessage();
        if (msg == null || msg.isEmpty()) return false;
        for (String contain : getExpectations()) {
            if (!Objects.equals(contain, "") && msg.contains(contain)) {
                return true;
            }
        }
        return false;
    }
}
