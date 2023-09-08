package com.homemylove.chino.plugin;

import com.homemylove.chino.annotations.Match;
import com.homemylove.chino.annotations.Param;
import com.homemylove.chino.annotations.Plugin;
import com.homemylove.chino.annotations.Priority;
import com.homemylove.chino.api.ApiChan;
import com.homemylove.chino.enums.PRIORITY;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

@Plugin
@Slf4j
public class ReplyPlugin {

    @Resource
    private ApiChan apiChan;

    @Match(equals = {"智乃","chino"})
    @Priority(PRIORITY.IMPORTANT)
    public String reply(){
        return "叫我干嘛？";
    }

    @Match(regex = "智乃{key}")
    @Priority(PRIORITY.IMPORTANT)
    public void reply(Long groupId, @Param("key") String key){

    }

}
