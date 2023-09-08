package com.homemylove.chino.adapter;

import com.homemylove.chino.entities.Message;
import com.homemylove.chino.handler.AbstractPluginHandler;
import com.homemylove.chino.handler.PluginHandler;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class PluginAdapter {

    public PluginAdapter(){
        this.handlers = new LinkedList<>();
    }

    @Setter
    private int priority = 0;

    private final List<PluginHandler> handlers;

    public void addPluginHandler(PluginHandler handler){
        if(handler==null) return;

        if(handlers.isEmpty() || !handlers.contains(handler)) {
            handlers.add(handler);
            setPriority(getPriority() +( (AbstractPluginHandler<?>) handler).getOrder());
        }
    }

    // 如果 有 @Anyway 让他试试
    public boolean supports(Message message){
        boolean result = false;
        if(this.priority % 2 == 1) return true;  // 奇数 有 Anyway
        for (PluginHandler handler : handlers) {
            result = handler.match(message);
            if(!result) return false;
        }
        return true;
    }
}
