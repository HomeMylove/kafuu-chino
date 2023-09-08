package com.homemylove.chino.handler;

import com.homemylove.chino.entities.Message;

import java.util.Objects;

public class StartWithHandler extends AbstractPluginHandler<String> {

    public StartWithHandler(String[] expectation){
        super(expectation);
    }
    @Override
    public boolean match(Message message) {
        // 获取 msg 比较前缀
        String msg = message.getMessage();
        if (isEmpty(msg)) return false;
        for (String prefix : getExpectations()) {
            if (!Objects.equals(prefix, "") && msg.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
