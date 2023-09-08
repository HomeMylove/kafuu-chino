package com.homemylove.chino.handler;

import com.homemylove.chino.entities.Message;

import java.util.Objects;

/**
 * Contains 处理器
 * 包含时通过
 */
public class ContainsHandler extends AbstractPluginHandler<String>{

    public ContainsHandler(String[] expectations){
        super(expectations);
    }

    @Override
    public boolean match(Message message) {
        String msg = message.getMessage();
        if (isEmpty(msg)) return false;
        for (String contain : getExpectations()) {
            if (!Objects.equals(contain, "") && msg.contains(contain)) {
                return true;
            }
        }
        return false;
    }
}
