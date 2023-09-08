package com.homemylove.chino.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 直接将Plugin的返回值发送
 */
@Component
@Aspect
@Slf4j
public class PluginAdvice {


//    @After("plugin()")
    public void say(){
        log.info("我调用啦");
    }

    @Pointcut("@annotation(com.homemylove.chino.annotations.Match)")
    public void plugin(){}


}
