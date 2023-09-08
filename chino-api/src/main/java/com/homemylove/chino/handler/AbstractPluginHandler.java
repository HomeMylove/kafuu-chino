package com.homemylove.chino.handler;

import com.homemylove.chino.entities.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public abstract class AbstractPluginHandler<T> implements PluginHandler {

    private int order;

    private T[] expectations;

    public AbstractPluginHandler(T[] expectations){
        this.expectations = expectations;
    }

}
