package com.homemylove.chino.handler;

import com.homemylove.chino.entities.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Data
public abstract class AbstractPluginHandler<T> implements PluginHandler {

    private int order;

    private T[] expectations;

    public AbstractPluginHandler(T[] expectations){
        this.expectations = expectations;
    }

    /**
     * 判断值是否为空
     * @param t 内容
     * @return 是否为空
     */
    protected boolean isEmpty(T t){
        if(t == null) return true;
        if(t instanceof String){
            return ((String) t).isEmpty();
        }
        if(t instanceof List<?>){
            return ((List<?>) t).isEmpty();
        }
        if(t instanceof Map<?,?>){
            return ((Map<?, ?>) t).isEmpty();
        }
        return true;
    }

}
