package com.homemylove.chino.dispatcher;

import com.homemylove.chino.adapter.EventNode;
import com.homemylove.chino.adapter.PluginAdapter;
import com.homemylove.chino.adapter.PluginNode;
import com.homemylove.chino.annotations.Match;
import com.homemylove.chino.annotations.Param;
import com.homemylove.chino.api.ApiChan;
import com.homemylove.chino.entities.Message;
import com.homemylove.chino.entities.Meta;
import com.homemylove.chino.proterties.ChinoControlProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class PluginDispatcher implements DispatcherAble {

    @Resource
    private PluginLoader pluginLoader;

    @Resource
    private TypeFactory typeFactory;

    @Resource
    private ApiChan apiChan;

    /**
     * 用于获取参数名
     */
    private static final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    private final Class<?> msgClazz = Message.class;

    public void dispatcher(Message message) {
        if("message".equals(message.getPostType())) dispatcherPlugin(message);
        if("notice".equals(message.getPostType())) dispatcherEvent(message);
    }

    public void dispatcherPlugin(Message message) {
        List<PluginNode> pluginList = pluginLoader.getPluginList();
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
                    // 装配参数
                    Object[] args = new Object[parameterNames.length];

                    for (int i = 0; i < parameterNames.length; i++) {
                        Parameter parameter = parameters[i];

                       if( Meta.class.equals(parameter.getType())){
                           args[i] = typeFactory.getMeta(message.getSelfId());
                           continue;
                       }else if (Message.class.equals(parameter.getType())) {
                           args[i] = message;
                           continue;
                       }

                        // 获取参数
                        // 看这个参数有没有 @Param 注解
                        Param param = parameter.getAnnotation(Param.class);
                        if (param != null) {
                            String value = param.value();
//                            // 取出那部分的值
//                            // 取出 regex
                            String regex = rawRegex.replace("{" + value + "}", "([\\w\\S\\u4E00-\\u9FA5]+)")
                                    .replaceAll("\\{.*?\\}", "[\\\\w\\\\S\\\\u4E00-\\\\u9FA5]+")
                                    .replaceAll("\\s+", "\\\\s+");

                            regex = "^" + regex + "$";
                            Pattern compile = Pattern.compile(regex);
                            Matcher matcher = compile.matcher(message.getMessage());

                            if (matcher.matches()) {
                                // 处理参数类型
                                args[i] = parse(matcher.group(1), parameter.getType());
                            }
                        } else {
                            String parameterName = parameterNames[i];
                            Field field = msgClazz.getDeclaredField(parameterName);
                            field.setAccessible(true);
                            Object o = field.get(message);
                            args[i] = parse(o, parameter.getType());
                        }
                        // 判断是不是特殊值
                    }
                    Object invoke = method.invoke(bean, args);
                    Class<?> returnType = method.getReturnType();
                    // 如果返回为String，试着将其发送
                    if(returnType.equals(String.class) && !"".equals(invoke)){
                        apiChan.sendGroupMsg(message.getGroupId(),(String) invoke);
                    } else if (returnType.equals(Boolean.class)) {
                        // 如果返回为 Boolean 且为false 继续往下走
                        boolean ret = (Boolean) invoke;
                        if(!ret) continue;
                    }
                    return;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("error" + e.getMessage());
        }
    }

    public void dispatcherEvent(Message message) {
        List<EventNode> eventList = pluginLoader.getEventList();
        for (EventNode eventNode : eventList) {
            try {
                if (eventNode.getHandler().match(message)) {

                    Object bean = eventNode.getBean();
                    Method method = eventNode.getMethod();
                    method.setAccessible(true);

                    String[] parameterNames = discoverer.getParameterNames(method);
                    Parameter[] parameters = method.getParameters();

                    assert parameterNames != null;
                    // 装配参数
                    Object[] args = new Object[parameterNames.length];

                    for (int i = 0; i < parameterNames.length; i++) {
                        Parameter parameter = parameters[i];

                        if( Meta.class.equals(parameter.getType())){
                            args[i] = typeFactory.getMeta(message.getSelfId());
                            continue;
                        } else if (Message.class.equals(parameter.getType())) {
                            args[i] = message;
                            continue;
                        }

                        String parameterName = parameterNames[i];
                        Field field = msgClazz.getDeclaredField(parameterName);
                        field.setAccessible(true);
                        Object o = field.get(message);
                        args[i] = parse(o, parameter.getType());
                    }
                    Object invoke = method.invoke(bean, args);
                    Class<?> returnType = method.getReturnType();
                    // 如果返回为String，试着将其发送
                    if(returnType.equals(String.class) && !"".equals(invoke)){
                        apiChan.sendGroupMsg(message.getGroupId(),(String) invoke);
                    } else if (returnType.equals(Boolean.class)) {
                        // 如果返回为 Boolean 且为false 继续往下走
                        boolean ret = (Boolean) invoke;
                        if(!ret) continue;
                    }
                    return;
                }
            } catch (Exception e) {
                throw new RuntimeException("error" + e.getMessage());
            }
        }
    }



    private <T> T parse(Object value, Class<T> clazz) {
        switch (clazz.getName()) {
            case "java.lang.String":
                return (T) value.toString();
            case "java.lang.Integer":
            case "int":
                return (T) Integer.valueOf(value.toString());
            case "java.lang.Long":
            case "long":
                return (T) Long.valueOf(value.toString());
            default:
                return (T) value;
        }
    }
}
