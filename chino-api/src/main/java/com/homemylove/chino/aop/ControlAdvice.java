package com.homemylove.chino.aop;

import com.homemylove.chino.api.MsgInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
@Aspect
public class ControlAdvice {

    @Resource
    private RedisTemplate<String,MsgInfo> redisTemplate;

    private final String prefix ="control:";

    /**
     * 在发送消息之后，在redis 中添加一条记录
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("afterSendMsg()")
    public Object addRecord(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 参数名
        String[] names = signature.getParameterNames();
        // 参数值
        Object[] args = joinPoint.getArgs();

        MsgInfo info = new MsgInfo();
        info.setTime(new Date());
        String groupId = "";
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            Object arg = args[i];
            if("groupId".equals(name)){
                groupId =  arg.toString();
            }else if("message".equals(name)){
                info.setMessage(arg.toString());
            }
        }
        Object ret = joinPoint.proceed();
        redisTemplate.opsForList().leftPush(prefix + groupId,info);
        return ret;
    }


    @Before("beforeHandle()")
    public void check(JoinPoint joinPoint) throws Throwable{
        // 参数值
        System.out.println("hello");
//        Message message = (Message)joinPoint.getArgs()[0];
//        Long groupId = message.getGroupId();
//
//        System.out.println("groupis" + groupId);
//        return joinPoint.proceed();
    }

    @Pointcut("execution(* com.homemylove.chino.api.ApiChan.sendGroupMsg(..))")
    public void afterSendMsg(){}


    @Pointcut("execution(void com.homemylove.chino.dispatcher.PluginDispatcher.handleMessage(..))")
    public void beforeHandle(){}

}
