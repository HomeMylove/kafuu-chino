package com.homemylove.chino.handler;

import com.homemylove.chino.entities.Message;

public class EventHandler extends AbstractPluginHandler<String>{

    public EventHandler(String type){
        super(new String[]{type});
    }
    @Override
    public boolean match(Message message) {
        String type = getExpectations()[0];
        String postType = message.getSubType();
        if(isEmpty(postType)) return false;
        return postType.equals(type);
    }
}
