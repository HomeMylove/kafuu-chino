package com.homemylove.chino.adapter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PluginNode {

    private Object bean;

    private Method method;

    private PluginAdapter pluginAdapter;

}
