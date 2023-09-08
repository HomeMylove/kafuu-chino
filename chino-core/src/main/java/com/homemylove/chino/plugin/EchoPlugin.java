package com.homemylove.chino.plugin;

import com.homemylove.chino.annotations.Anyway;
import com.homemylove.chino.annotations.Match;
import com.homemylove.chino.annotations.Param;
import com.homemylove.chino.annotations.Plugin;
import com.homemylove.chino.api.ApiChan;
import com.homemylove.chino.api.MessageBuilder;
import com.homemylove.chino.entities.Echo;
import com.homemylove.chino.proterties.ChinoControlProperties;
import com.homemylove.chino.service.impl.EchoService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;


@Plugin
@Slf4j
public class EchoPlugin {
    @Resource
    private ApiChan apiChan;

    @Resource
    private EchoService echoService;

    private final Long MAX_ECHO = 10L;

    @Resource
    private ChinoControlProperties controlProperties;

    @Match(regex = "echo {question} {answer}")
    public String chi(@Param("question") String question,
                    @Param("answer") String answer,
                    Long groupId,
                    Long userId){
        log.info("echo .....:{}",question);
        // 发起 echo
        long countEcho = echoService.countEcho(groupId, userId);
        if(countEcho >= 10L){
            return new MessageBuilder().at(userId)
                    .message("添加失败，每个人最多有").message(MAX_ECHO.toString()).message("条").build();

        }else {
            // 不足
            Echo echo = new Echo(null,groupId,userId,answer,question);
            int i = echoService.addEcho(echo);
            if(i > 0) return new MessageBuilder().at(userId).message("添加成功！！以后你可以对我说：").message(question).build();
            return null;
        }
    }

    @Anyway
    @Match
    public Boolean answer(Long groupId,String message){
        log.info("message:{}",message);
        // 查找
        String answer = echoService.getAnswer(groupId,message);
        if(answer!=null){
            apiChan.sendGroupMsg(groupId,answer);
            return true;
        }
        return false;
    }

    @Match(regex = "delete {question}")
    public String deleteEcho(Long groupId,Long userId,@Param("question")String question){
        boolean yes = controlProperties.getSuperUser().contains(userId);
        boolean result = echoService.deleteEcho(groupId,yes ? null : userId,question) > 0;
        return new MessageBuilder().at(userId)
                .message(result ? "删除成功" : "删除失败").build();

    }
}
