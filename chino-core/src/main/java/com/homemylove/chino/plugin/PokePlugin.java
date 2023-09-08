package com.homemylove.chino.plugin;

import com.google.common.base.Joiner;
import com.homemylove.chino.annotations.Event;
import com.homemylove.chino.annotations.Plugin;
import com.homemylove.chino.api.ApiChan;
import com.homemylove.chino.api.MessageBuilder;
import com.homemylove.chino.entities.Meta;
import com.homemylove.chino.entities.Result;
import com.homemylove.chino.entities.data.GroupMemberInfo;
import com.homemylove.chino.service.CallNameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Plugin
@Slf4j
public class PokePlugin {

    @Resource
    private ApiChan apiChan;

    @Resource
    private CallNameService callNameService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final String POKE_PREFIX = "poke";

    @Event
    public String poke(Long groupId, Long userId, Meta meta) {
        Long times = getPokeTimes(groupId, userId);

        // 主人戳
        if (userId.equals(meta.getMaster())) {
            return "主人，戳我干嘛呀(✿◡‿◡)";
        }

        String name = callNameService.getName(groupId, userId);
        if (name.isEmpty()) {
            Result<GroupMemberInfo> memberInfo = apiChan.getGroupMemberInfo(groupId, userId);
            name = memberInfo == null ? null : memberInfo.getData().getNickname();
        }

        if (meta.getSuperUsers().contains(userId)) {
            if (times < 3) {
                poke(groupId, userId, times);
                return name + ",你是主人认可的人，你想戳就戳吧(￣_,￣ )";
            } else {
                return new MessageBuilder("就算是你,").message(name).message(",也不能戳我这么多次┗(｀Дﾟ┗(｀ﾟДﾟ´)┛ﾟД´)┛").build();
            }
        } else {
            if (times < 3) {
                poke(groupId, userId, times);
                return "区区" + name + "也敢戳我？o(≧口≦)o ";
            }else {
                return "404 智乃已拒绝你的访问～(̿▀̿ ̿Ĺ̯̿̿▀̿ ̿)̄";
            }
        }
    }

    private String getKey(Long groupId, Long userId) {
        return Joiner.on(":").join(POKE_PREFIX, groupId, userId);
    }

    private Long getPokeTimes(Long groupId, Long userId) {
        Object o = redisTemplate.opsForValue().get(getKey(groupId, userId));
        return o == null ? 0 : Long.parseLong(o.toString());
    }

    private boolean poke(Long groupId, Long userId, Long times) {
        String key = getKey(groupId, userId);
        if (times == 0) {
            redisTemplate.opsForValue().set(key, 1L, 120, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().increment(key);
        }
        return true;
    }

}
