package com.homemylove.chino.service.impl;

import com.homemylove.chino.entities.Echo;

public interface EchoService {

    /**
     * 查询 echo 条数
     * @param groupId
     * @param userId
     * @return 条数
     */
    long countEcho(Long groupId,Long userId);

    /**
     * 编辑 echo 添加或更新
     * @param echo
     * @return
     */
    int addEcho(Echo echo);

    String getAnswer(Long groupId,String question);

    int deleteEcho(Long groupId,Long userId,String question);
}
