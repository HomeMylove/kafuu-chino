package com.homemylove.chino.adapter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * 插件节点，存放插件必须的内容
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PluginNode {

    /**
     * 方法的调用者，即 @Plugin 标注的类
     */
    private Object bean;

    /**
     * 具体方法，即 @Match 标注的类
     */
    private Method method;

    /**
     * 对应的插件适配器
     */
    private PluginAdapter pluginAdapter;

}
