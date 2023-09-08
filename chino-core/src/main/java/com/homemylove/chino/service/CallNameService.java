package com.homemylove.chino.service;

import com.homemylove.chino.entities.CallName;

public interface CallNameService {

    /**
     * 根据群号和QQ号查询名字,没有返回空串
     * @param groupId 群号
     * @param userId QQ号
     * @return name 或者 空字符串
     */
    String getName(Long groupId,Long userId);

    /**
     * 检查 name 是否存在
     * @param groupId 群号
     * @param name name
     * @return 存在则返回是谁
     */
    CallName nameExists(Long groupId,String name);

    /**
     * 设置名字
     * @param groupId 群号
     * @param userId QQ号
     * @param name 名字
     * @return 影响
     */
    int setName(Long groupId,Long userId,String name);

}
