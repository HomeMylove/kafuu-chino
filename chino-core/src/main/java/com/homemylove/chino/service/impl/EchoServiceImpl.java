package com.homemylove.chino.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.homemylove.chino.entities.Echo;
import com.homemylove.chino.mapper.EchoMapper;
import com.homemylove.chino.utils.ListUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class EchoServiceImpl implements EchoService {

    @Resource
    private EchoMapper echoMapper;

    @Override
    public long countEcho(Long groupId, Long userId) {
        return echoMapper.selectCount(new QueryWrapper<Echo>().eq("group_id", groupId).eq("user_id", userId));
    }

    @Override
    public int addEcho(Echo echo) {
        QueryWrapper<Echo> wrapper = new QueryWrapper<Echo>()
                .eq("group_id", echo.getGroupId())
                .eq("user_id", echo.getUserId())
                .eq("question", echo.getQuestion());
        // 直接拿去更新
        int updated = echoMapper.update(echo, wrapper);
        return updated > 0 ? updated : echoMapper.insert(echo);
    }

    @Override
    public String getAnswer(Long groupId, String question) {
        List<Echo> echos = echoMapper.selectList(new QueryWrapper<Echo>().select("answer").eq("group_id", groupId)
                .eq("question", question));
        return echos.isEmpty() ? null : ListUtil.getRandomEle(echos).getAnswer();
    }

    @Override
    public int deleteEcho(Long groupId,Long userId,String question) {
        QueryWrapper<Echo> wrapper = new QueryWrapper<Echo>()
                .eq("group_id", groupId)
                .eq(userId != null,"user_id", userId)
                .eq("question", question);
        return echoMapper.delete(wrapper);
    }
}
