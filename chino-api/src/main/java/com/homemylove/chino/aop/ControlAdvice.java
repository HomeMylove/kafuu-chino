package com.homemylove.chino.aop;

import com.google.common.base.Joiner;
import com.homemylove.chino.api.MsgInfo;
import com.homemylove.chino.entities.Message;
import com.homemylove.chino.proterties.ChinoControlProperties;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Slf4j
public class ControlAdvice {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ChinoControlProperties controlProperties;

    private final String FREQUENCY_PREFIX = "frequency";

    private final String SHUTUP_PREFIX = "ban";

    private final String POKE_PREFIX = "poke";

    /**
     * 在发送消息之后，在redis 中添加一条记录
     *
     * @param joinPoint ProceedingJoinPoint
     * @return 方法的返回值
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
            if ("groupId".equals(name)) {
                info.setGroupId((Long) arg);
                groupId = arg.toString();
            } else if ("message".equals(name)) {
                info.setMessage(arg.toString());
            }
        }
        Object ret = joinPoint.proceed();
        log.info("send message:{} to group:{}",info.getMessage(),groupId);
        List<Frequency> frequencies = getFrequency();
        for (Frequency frequency : frequencies) {
            String key = Joiner.on(":").join(FREQUENCY_PREFIX, groupId, frequency.seconds, frequency.times);
            redisTemplate.opsForList().leftPush(key, info);
        }
        return ret;
    }

    /**
     * 分发请求之前 检验能否继续
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Around("beforeHandle()")
    public Object check(ProceedingJoinPoint joinPoint) throws Throwable {
        // 参数值
        Message message = getMessage(joinPoint);

        Long groupId = message.getGroupId();
        Long userId = message.getUserId();
        boolean frequency = controlFrequency(groupId);
        boolean shutUp = controlShutUp(groupId, userId);
        boolean poke = controlPoke(groupId, userId);
        log.info("poke:{}",poke);
        if (frequency && shutUp && poke) {
            return joinPoint.proceed();
        } else {
            return null;
        }
    }

    @Pointcut("execution(* com.homemylove.chino.api.ApiChan.sendGroupMsg(..))")
    public void afterSendMsg() {
    }


    @Pointcut("execution(void com.homemylove.chino.dispatcher.PluginDispatcher.dispatcher(..))")
    public void beforeHandle() {
    }

    /**
     * 戳一戳反馈
     * @param groupId
     * @param userId
     * @return
     */
    public boolean controlPoke(Long groupId, Long userId){
        String key = Joiner.on(":").join(POKE_PREFIX,groupId,userId);
        Object o = redisTemplate.opsForValue().get(key);
        return o == null || Long.parseLong(o.toString()) < 3 || controlProperties.getMaster().equals(userId);
    }

    /**
     * 开启还是关闭
     *
     * @param groupId
     * @param userId
     * @return
     */
    private boolean controlShutUp(Long groupId, Long userId) {
        String key = Joiner.on(":").join(SHUTUP_PREFIX, groupId);
        Boolean ban = (Boolean) redisTemplate.opsForValue().get(key);
        // 否被 ban
        if (ban == null || !ban) return true;
        // 超级管理员通过
        List<Long> superUser = controlProperties.getSuperUser();
        return !superUser.isEmpty() && superUser.contains(userId);
    }

    private boolean controlFrequency(Long groupId) {
        List<Frequency> frequencies = getFrequency();
        Date now = new Date();
        log.info("加载频率：");
        for (Frequency frequency : frequencies) {
            log.info(">>>> {}条 / {}秒", frequency.times, frequency.seconds);
            String key = Joiner.on(":")
                    .join(FREQUENCY_PREFIX, groupId, frequency.seconds, frequency.times);
            // 如果没有数据或者条数较少，直接通过
            Long size = redisTemplate.opsForList().size(key);
            if (size == null || size < frequency.times) continue;
            // 条数多
            // 弹出
            while (true) {
                MsgInfo info = (MsgInfo) redisTemplate.opsForList().rightPop(key);
                if (info == null) break;
                if (less(info.getTime(), now, frequency.seconds)) {
                    // 插回去
                    redisTemplate.opsForList().rightPush(key, info);
                    break;
                }
                size--;
            }
            if (size >= frequency.times) {
                log.error("超过频率 {}条/{}秒 拒绝执行", frequency.times, frequency.seconds);
                return false;
            }
        }
        return true;
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class Frequency {
        private Long seconds;
        private Long times;
    }

    /**
     * 获取频率列表，不合格的会被剔除
     *
     * @return 频率列表
     */
    private List<Frequency> getFrequency() {
        List<Frequency> frequencies = new ArrayList<>();
        List<Long[]> frequency = controlProperties.getFrequency();
        for (Long[] longs : frequency) {
            if (longs.length < 2) continue;
            Long seconds = longs[0];
            Long times = longs[1];
            if (seconds <= 0 || times <= 0) continue;
            frequencies.add(new Frequency(seconds, times));
        }
        return frequencies;
    }

    /**
     * 判断两个时间是不是小于 seconds
     *
     * @param time1   时间1
     * @param time2   时间2
     * @param seconds 给定的时间间隔
     * @return 判断
     */
    private boolean less(Date time1, Date time2, Long seconds) {
        Duration between = Duration.between(time1.toInstant(), time2.toInstant());
        return Math.abs(between.getSeconds()) < seconds;
    }


    /**
     * 获取 Message 对象
     *
     * @param joinPoint ProceedingJoinPoint
     * @return Message
     */
    private Message getMessage(ProceedingJoinPoint joinPoint) {
        Message message = null;

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 参数名
        Class<?>[] types = signature.getParameterTypes();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            if (type == Message.class) {
                message = (Message) args[i];
            }
        }
        return message;
    }
}
