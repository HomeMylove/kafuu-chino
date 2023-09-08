package com.homemylove.chino.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homemylove.chino.adapter.PluginAdapter;
import com.homemylove.chino.adapter.PluginNode;
import com.homemylove.chino.annotations.*;
import com.homemylove.chino.api.MsgInfo;
import com.homemylove.chino.entities.Message;
import com.homemylove.chino.handler.*;
import com.homemylove.chino.proterties.ChinoControlProperties;
import com.homemylove.chino.proterties.ChinoPluginProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class PluginDispatcher {
    private static final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    private static final List<PluginNode> pluginList = new ArrayList<>();

    private final Class<?> msgClazz = Message.class;

    @Resource
    private ChinoPluginProperties.Priority priority;

    @Resource
    private ChinoControlProperties controlProperties;

    @Resource
    private ApplicationContext context;

    @Resource
    private RedisTemplate redisTemplate;


    @PostConstruct
    public void loadPlugin() {
        // 获取含有 @Plugin 注解的插件类
        String[] plugins = context.getBeanNamesForAnnotation(Plugin.class);

        for (String name : plugins) {
            // 获取 clazz
            Class<?> clazz = context.getType(name);
            log.info(">>> plugin name:{}\tplugin type:{}", name, clazz);

            assert clazz != null;
            // 获取 bean 存入容器
            Object plugin = context.getBean(name, clazz);
            // 每一个方法都要映射
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Match.class)) {
                    log.info(">>> Method:{}", method.getName());
                    PluginAdapter adapter = getPluginAdapter(method);
                    if (adapter.getPriority() == 0) continue;
                    pluginList.add(new PluginNode(plugin, method, adapter));
                }
            }
        }
        Collections.sort(pluginList, new Comparator<PluginNode>() {
            @Override
            public int compare(PluginNode o1, PluginNode o2) {
                return o2.getPluginAdapter().getPriority() - o1.getPluginAdapter().getPriority();
            }
        });

        int i = 1;
        for (PluginNode pluginNode : pluginList) {
            log.info("i:{}",i++);
            log.info(pluginNode.getMethod().getName());
            log.info("priority:{}", pluginNode.getPluginAdapter().getPriority());
        }
    }

    private PluginAdapter getPluginAdapter(Method method) {
        Match match = method.getAnnotation(Match.class);
        String[] contains = match.contains();
        String[] startWith = match.startWith();
        String[] endWith = match.endWith();
        String regex = match.regex();
        String[] equals = match.equals();

        PluginAdapter adapter = new PluginAdapter();
        if (startWith.length > 0) {
            StartWithHandler startWithHandler = new StartWithHandler(startWith);
            startWithHandler.setOrder(priority.getStartWith() << 1);
            adapter.addPluginHandler(startWithHandler);
        }
        if (endWith.length > 0) {
            EndWithHandler endWithHandler = new EndWithHandler(endWith);
            endWithHandler.setOrder(priority.getEndWith() << 1);
            adapter.addPluginHandler(endWithHandler);
        }
        if (contains.length > 0) {
            ContainsHandler containsHandler = new ContainsHandler(contains);
            containsHandler.setOrder(priority.getContains() << 1);
            adapter.addPluginHandler(containsHandler);
        }
        if (!"".equals(regex)) {
            RegexHandler regexHandler = new RegexHandler(regex);
            regexHandler.setOrder(priority.getRegex() << 1);
            adapter.addPluginHandler(regexHandler);
        }
        if (equals.length > 0) {
            EqualsHandler equalsHandler = new EqualsHandler(equals);
            equalsHandler.setOrder(priority.getEquals() << 1);
            adapter.addPluginHandler(equalsHandler);
        }

        // 如果有 @Anyway 试一试
        if (method.isAnnotationPresent(Anyway.class)) {
            AnywayHandler anywayHandler = new AnywayHandler();
            anywayHandler.setOrder(1);
            adapter.addPluginHandler(anywayHandler);
        }
        // 如果有 @Priority 增加优先级
        if (method.isAnnotationPresent(Priority.class)) {
            Priority priorityAnno = method.getAnnotation(Priority.class);
            adapter.setPriority(adapter.getPriority() + priorityAnno.value().getValue());
        }

        return adapter;
    }

    private boolean lessThan(Date time1,Date time2){
        Duration between = Duration.between(time1.toInstant(), time2.toInstant());
        return Math.abs(between.getSeconds()) < 60L;
    }

    public void handleMessage(Message message) {
        Long groupId = message.getGroupId();
        Date now = new Date();
        String key = "control:" + groupId;
        String banKey = "ban:" + groupId;

        Long userId = message.getUserId();
        List<Long> superUser = controlProperties.getSuperUser();
        if(!superUser.contains(userId)){
            // 判断是不是禁止了
            Boolean ban = (Boolean)redisTemplate.opsForValue().get(banKey);
            if(ban != null && ban){
                return;
            }
        }

        while (true){
            MsgInfo last = (MsgInfo) redisTemplate.opsForList().rightPop(key);
            if(last!=null){
                if(lessThan(now,last.getTime())){
                    redisTemplate.opsForList().rightPush(key,last);
                    break;
                }
            }else {
                break;
            }
        }
        // 判断是不是10条
        Long size = redisTemplate.opsForList().size(key);
        if(size!= null && size >= 10){
            log.info("超出限制");
            return;
        }

        try {
            for (PluginNode node : pluginList) {
                PluginAdapter adapter = node.getPluginAdapter();

                if (adapter.supports(message)) {
                    Object bean = node.getBean();
                    Method method = node.getMethod();
                    method.setAccessible(true);

                    // 获取正则
                    String rawRegex = method.getAnnotation(Match.class).regex();

                    String[] parameterNames = discoverer.getParameterNames(method);
                    Parameter[] parameters = method.getParameters();


                    assert parameterNames != null;
                    Object[] args = new Object[parameterNames.length];

                    for (int i = 0; i < parameterNames.length; i++) {
                        Parameter parameter = parameters[i];
                        // 获取参数
                        // 看这个参数有没有 @Param 注解
                        Param param = parameter.getAnnotation(Param.class);
                        if (param != null) {
                            String value = param.value();
//                            // 取出那部分的值
//                            // 取出 regex
                            String regex = rawRegex.replace("{" + value + "}", "([\\w\\u4E00-\\u9FA5]+)")
                                    .replaceAll("\\{.*?\\}", "[\\\\w\\\\u4E00-\\\\u9FA5]+")
                                    .replaceAll("\\s+", "\\\\s+");

                            regex = "^" + regex + "$";
                            Pattern compile = Pattern.compile(regex);
                            Matcher matcher = compile.matcher(message.getMessage());

                            if (matcher.matches()) {
                                args[i] = matcher.group(1);
                                continue;
                            }
                        }
                        // 判断是不是特殊值

                        String parameterName = parameterNames[i];
                        Field field = msgClazz.getDeclaredField(parameterName);
                        field.setAccessible(true);
                        Object o = field.get(message);
                        args[i] = o;
                    }

                    Object invoke = method.invoke(bean, args);
                    return;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("error" + e.getMessage());
        }
    }

    @RabbitListener(queues = "${chino.listen.rabbitmq.name:public}")
    public void listen(String request) {
        log.info("收到消息:{}", request);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Message message = objectMapper.readValue(request, Message.class);
            message.setMessage(message.getMessage().trim());

            log.info("=======================");
            handleMessage(message);
        } catch (Exception e) {
            log.error("error" + e);
        }

    }
}
