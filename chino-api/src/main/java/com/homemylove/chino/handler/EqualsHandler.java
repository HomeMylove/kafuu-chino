package com.homemylove.chino.handler;

import com.homemylove.chino.entities.Message;

import java.util.Objects;

public class EqualsHandler extends AbstractPluginHandler<String>{

    public EqualsHandler(String[] expectations){
        super(expectations);
    }

    @Override
    public boolean match(Message message) {
        String msg = message.getMessage();
        if (isEmpty(msg)) return false;
        for (String exp : getExpectations()) {
            if (!Objects.equals(exp, "") && msg.equals(exp)) {
                return true;
            }
        }
        return false;
    }
}
