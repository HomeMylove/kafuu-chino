package com.homemylove.chino.plugin;

import com.homemylove.chino.annotations.Match;
import com.homemylove.chino.annotations.Param;
import com.homemylove.chino.annotations.Plugin;
import com.homemylove.chino.annotations.Priority;
import com.homemylove.chino.api.ApiChan;
import com.homemylove.chino.api.MessageBuilder;
import com.homemylove.chino.entities.CallName;
import com.homemylove.chino.entities.Result;
import com.homemylove.chino.entities.data.GroupMemberInfo;
import com.homemylove.chino.enums.PRIORITY;
import com.homemylove.chino.service.CallNameService;
import com.homemylove.chino.utils.ListUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Plugin
@Slf4j
public class CallNamePlugin {
    @Resource
    private CallNameService callNameService;

    @Resource
    private ApiChan apiChan;

    private static final List<String> replyList = new ArrayList<>();

    private static final List<String> badName = new ArrayList<>();

    static {
        replyList.add("智乃当然知道你是谁啦～，你就是{wave}吧(≧∇≦)ﾉ");
        replyList.add("智乃当然记得你呀～，你就是{wave}吧(≧∇≦)ﾉ");
        replyList.add("智乃记得你，{wave}，{name}也要记得智乃哦☆✲ﾟ｡(((´♡‿♡`+)))｡ﾟ✲☆");

        badName.add("狗剩");
        badName.add("铁蛋");
        badName.add("春花");
        badName.add("虎子");
        badName.add("大妞");
        badName.add("傻柱");
        badName.add("金莲");
        badName.add("阿宝");
    }

    @Match(equals = {"智乃我是谁", "我是谁智乃"})
    @Priority(PRIORITY.IMPORTANT)
    public String callName(Long groupId, Long userId) {
        // 查询数据库
        String name = callNameService.getName(groupId, userId);
        if (name.isEmpty()) {
            // 调用 api
            Result<GroupMemberInfo> result = apiChan.getGroupMemberInfo(groupId, userId);
            if (result == null || (name = result.getData().getNickname()).isEmpty()) {
                return "智乃不知道你是谁呢(ο´･д･)??";
            }
        }
        String reply = ListUtil.getRandomEle(replyList).replaceAll("\\{name\\}", name)
                .replaceAll("\\{wave\\}", addWaves(name));
        return new MessageBuilder().at(userId).message(reply).build();
    }

    @Match(regex = "智乃以后叫我{name}")
    @Priority(PRIORITY.IMPORTANT)
    public String changeName(Long groupId, Long userId, @Param("name") String name) {
        CallName nameExists = callNameService.nameExists(groupId, name);
        String message = "";
        // name 存在
        if (nameExists != null) {
            String newName = findNewName(groupId);
            // 别人的
            if (!Objects.equals(nameExists.getUserId(), userId)) {
                message = new MessageBuilder().at(userId)
                        .message("对不起，智乃不能叫你").message(name)
                        .message(",因为")
                        .at(nameExists.getUserId()).message("也叫这个，换一个名字吧!")
                        .message(!newName.isEmpty(), "你觉得”" + newName + "“怎么样呢？ (>‿◠)\"").build();
            } else {
                message = new MessageBuilder().at(userId)
                        .message("你本来就叫").message(name).message("呀？(＠_＠;)").message("换一个名字吧!")
                        .message(!newName.isEmpty(), "你觉得”" + newName + "“怎么样呢？ (>‿◠)\"").build();
            }
        } else {
            // 不存在
            int setName = callNameService.setName(groupId, userId, name);
            MessageBuilder mb = new MessageBuilder().at(userId);
            if (setName > 0) {
                if(badName.contains(name)){
                    mb.message("好的").message(addWaves(name)).message("ヾ(≧▽≦*)o");
                }else {
                mb.message("好的，我以后就叫你").message(name).message("啦");
                }
            }else {
                mb.message("改名失败了呢，〒▽〒");
            }
            message = mb.build();
        }
        return message;
    }

    /**
     * 返回一个推荐的名字，没有合适的就返回空串
     *
     * @param groupId 群号
     * @return 名字
     */
    private String findNewName(Long groupId) {
        for (String name : badName) {
            CallName nameExists = callNameService.nameExists(groupId, name);
            if (nameExists == null) return name;
        }
        return "";
    }

    /**
     * 给文字添加波
     *
     * @return 修改之后的名字
     */
    private String addWaves(String name) {
        if (name.length() <= 5) {
            char[] array = name.toCharArray();
            StringBuilder sb = new StringBuilder();
            for (char c : array) {
                sb.append(c).append("～");
            }
            return sb.toString();
        } else {
            return name;
        }
    }

}
