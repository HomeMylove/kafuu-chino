package com.homemylove.chino.dispatcher;

import com.homemylove.chino.entities.Meta;
import com.homemylove.chino.proterties.ChinoControlProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 处理特殊类型
 */
@Component
public class TypeFactory {

    @Resource
    private  ChinoControlProperties controlProperties;

    /**
     * 返回元信息
     * @param selfId Bot qq 可以从 Message 拿
     * @return Meta
     */
    public  Meta getMeta(Long selfId){
        Meta meta = new Meta();
        meta.setMaster(controlProperties.getMaster());
        meta.setSuperUsers(controlProperties.getSuperUser());
        meta.setSelfId(selfId);
        return meta;
    }
}
