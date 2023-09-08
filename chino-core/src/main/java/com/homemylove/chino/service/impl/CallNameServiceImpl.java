package com.homemylove.chino.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.homemylove.chino.entities.CallName;
import com.homemylove.chino.mapper.CallNameMapper;
import com.homemylove.chino.service.CallNameService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CallNameServiceImpl implements CallNameService {

    @Resource
    private CallNameMapper callNameMapper;

    @Override
    public String getName(Long groupId, Long userId) {
        QueryWrapper<CallName> wrapper = new QueryWrapper<CallName>().select("name")
                .eq("group_id", groupId)
                .eq("user_id", userId);
        CallName one = callNameMapper.selectOne(wrapper);
        if (one == null) return "";
        return one.getName();
    }

    @Override
    public CallName nameExists(Long groupId, String name) {
        QueryWrapper<CallName> wrapper = new QueryWrapper<CallName>()
                .select("id", "user_id")
                .eq("group_id", groupId)
                .eq("name", name);
        return callNameMapper.selectOne(wrapper);
    }

    @Override
    public int setName(Long groupId, Long userId, String name) {
        CallName one = callNameMapper.selectOne(new QueryWrapper<CallName>()
                .eq("group_id", groupId)
                .eq("user_id", userId));

        if (one != null) {
            one.setName(name);
            return callNameMapper.updateById(one);
        } else {
            one = new CallName(null, groupId, userId, name);
            return callNameMapper.insert(one);
        }
    }
}
