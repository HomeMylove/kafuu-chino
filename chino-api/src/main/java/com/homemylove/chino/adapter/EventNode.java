package com.homemylove.chino.adapter;


import com.homemylove.chino.handler.EventHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EventNode {
    private Object bean;
    private Method method;
    private EventHandler handler;
}
