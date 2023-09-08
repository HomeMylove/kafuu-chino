package com.homemylove.chino.plugin;

import com.homemylove.chino.annotations.Match;
import com.homemylove.chino.annotations.Plugin;
import com.homemylove.chino.annotations.Priority;
import com.homemylove.chino.api.ApiChan;
import com.homemylove.chino.enums.PRIORITY;
import com.homemylove.chino.proterties.ChinoControlProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@Slf4j
@Plugin
public class ControlPlugin {
    @Resource
    private ApiChan apiChan;
    @Resource
    private ChinoControlProperties chinoControlProperties;
    @Resource
    private RedisTemplate<String, Object> banRedisTemplate;
    private final String prefix = "ban:";

    @Match(regex = "智乃闭嘴")
    @Priority(PRIORITY.IMPORTANT)
    public String shutUp(Long groupId, Long userId) {
        if (auth(userId)) {
            Boolean ban = (Boolean) banRedisTemplate.opsForValue().get(prefix + groupId);
            if (ban != null && ban) {
                return  "智乃已经闭嘴啦╥﹏╥...";
            } else {
                banRedisTemplate.opsForValue().set(prefix + groupId, true);
                return "智乃闭嘴啦～";
            }
        } else {
            return "你是谁啊也想让智乃闭嘴￣へ￣";
        }
    }

    @Match(regex = "智乃说话")
    @Priority(PRIORITY.IMPORTANT)
    public String openMouth(Long groupId, Long userId) {
        if (auth(userId)) {
            Boolean ban = (Boolean) banRedisTemplate.opsForValue().get(prefix + groupId);
            if (ban != null && ban) {
                banRedisTemplate.opsForValue().set(prefix + groupId, false);
                return "智乃复活啦(*^▽^*)";
            }else {
            return "智乃还没闭嘴呢～<(￣︶￣)>";
            }
        }
        return null;
    }

    private boolean auth(Long userId) {
        return chinoControlProperties.getSuperUser().contains(userId);
    }


}
