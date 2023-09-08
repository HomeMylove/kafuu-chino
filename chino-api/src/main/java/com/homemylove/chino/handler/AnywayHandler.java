package com.homemylove.chino.handler;

import com.homemylove.chino.entities.Message;

public class AnywayHandler extends AbstractPluginHandler<Object>{

    @Override
    public boolean match(Message message) {
        return true;
    }
}
