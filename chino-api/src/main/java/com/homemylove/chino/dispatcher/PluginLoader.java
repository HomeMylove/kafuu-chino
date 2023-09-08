package com.homemylove.chino.dispatcher;

import com.homemylove.chino.adapter.EventNode;
import com.homemylove.chino.adapter.PluginAdapter;
import com.homemylove.chino.adapter.PluginNode;
import com.homemylove.chino.annotations.*;
import com.homemylove.chino.enums.EVENT_TYPE;
import com.homemylove.chino.handler.*;
import com.homemylove.chino.proterties.ChinoPluginProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
public class PluginLoader  {
    @Getter
    private final List<PluginNode> pluginList = new ArrayList<>();

    @Getter
    private final List<EventNode> eventList = new ArrayList<>();

    @Resource
    private ChinoPluginProperties.Priority priority;

    @Resource
    private ApplicationContext context;

    @PostConstruct
    private void loadPlugin() {
        // 获取含有 @Plugin 注解的插件类
        String[] beans = context.getBeanNamesForAnnotation(Plugin.class);
        // 将 bean 上的方法加入 pluginList
        addToPluginList(beans);
        // 按照优先级排序
        sortPluginList();
    }

    /**
     * 将 pluginList 内的适配器按照优先级排序
     */
    private void sortPluginList() {
        pluginList.sort(new Comparator<PluginNode>() {
            @Override
            public int compare(PluginNode o1, PluginNode o2) {
                return o2.getPluginAdapter().getPriority() - o1.getPluginAdapter().getPriority();
            }
        });
    }

    /**
     * 添加到 pluginList
     *
     * @param beans bean names
     */
    private void addToPluginList(String[] beans) {
        for (String name : beans) {
            // 获取 clazz
            Class<?> clazz = context.getType(name);
            log.info(">>>load plugin name:{}\tplugin type:{}", name, clazz);
            assert clazz != null;
            // 获取 bean 存入列表
            Object plugin = context.getBean(name, clazz);
            // 每一个方法都要映射
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Match.class)) {
                    log.info(">>> load Method:{}", method.getName());
                    PluginAdapter adapter = getPluginAdapter(method);
                    // 如果这个适配器没有优先级，不添加
                    if (adapter.getPriority() == 0) continue;
                    pluginList.add(new PluginNode(plugin, method, adapter));
                } else if (method.isAnnotationPresent(Event.class)) {
                    log.info(">>> load Event:{}",method.getName());
                    EventNode eventNode = getEventNode(method,plugin);
                    if(eventNode == null) continue;
                    eventList.add(eventNode);
                }
            }
        }
    }

    private EventNode getEventNode(Method method,Object bean){
        Event ev = method.getAnnotation(Event.class);
        EVENT_TYPE eventType = ev.value();
//        保存到事件处理器
        if(eventType != null){
            EventHandler eventHandler = new EventHandler(eventType.getType());
            return new EventNode(bean,method,eventHandler);
        }
        return null;
    }



    /**
     * 获取方法的适配器
     *
     * @param method 方法
     * @return 适配器
     */
    private PluginAdapter getPluginAdapter(Method method) {
        Match match = method.getAnnotation(Match.class);
        // 获取 Match 内的值
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
            anywayHandler.setOrder(1);  // 设为 1 取模用
            adapter.addPluginHandler(anywayHandler);
        }
        // 如果有 @Priority 增加优先级
        if (method.isAnnotationPresent(Priority.class)) {
            Priority priorityAnno = method.getAnnotation(Priority.class);
            adapter.setPriority(adapter.getPriority() + priorityAnno.value().getValue());
        }

        return adapter;
    }
}
