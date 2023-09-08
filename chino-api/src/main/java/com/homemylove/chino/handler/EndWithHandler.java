package com.homemylove.chino.handler;

import com.homemylove.chino.entities.Message;

import java.util.Objects;

public class EndWithHandler extends AbstractPluginHandler<String>{

    public EndWithHandler(String[] expectations){
        super(expectations);
    }

    @Override
    public boolean match(Message message) {
        String msg = message.getMessage();
        if (isEmpty(msg)) return false;
        for (String suffix : getExpectations()) {
            if (!Objects.equals(suffix, "") && msg.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }
}
