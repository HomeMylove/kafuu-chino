package com.homemylove.chino.adapter;

import com.homemylove.chino.entities.Message;
import com.homemylove.chino.handler.AbstractPluginHandler;
import com.homemylove.chino.handler.PluginHandler;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件适配器，实现 Supports 校验
 */
@Getter
public class PluginAdapter implements Supports {

    public PluginAdapter() {
        this.handlers = new ArrayList<>();
    }

    /**
     * 优先级，值越大的适配器优先校验
     */
    @Setter
    private int priority = 0;

    /**
     * 插件处理器列表，存放处理器，在 supports 中全部调用
     */
    public final List<PluginHandler> handlers;

    /**
     * 添加处理器，为空或已存在时拒绝添加
     * 统计处理器的优先级
     *
     * @param handler 插件处理器
     */
    public void addPluginHandler(PluginHandler handler) {
        if (handler == null) return;
        if (handlers.isEmpty() || !handlers.contains(handler)) {
            handlers.add(handler);
            setPriority(getPriority() + ((AbstractPluginHandler<?>) handler).getOrder());
        }
    }

    /**
     * Supports 的实现
     * 依次校验所有处理器
     * 当有 @Anyway 标注时，跳过校验，直接通过
     * @param message message 正文
     * @return 如果适配器所有处理器都通过，返回 true
     */
    public boolean supports(Message message) {
        boolean result = false;
        // @Anyway 的优先级为 1 ，相加之后为奇数
        if (this.priority % 2 == 1) return true;
        for (PluginHandler handler : handlers) {
            // 只要不符合，立刻退出
            if (!handler.match(message)) return false;
        }
        return true;
    }
}
