package com.homemylove.chino.handler;

import com.homemylove.chino.entities.Message;

public interface PluginHandler {
    boolean match(Message message);
}
