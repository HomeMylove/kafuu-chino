package com.homemylove.chino.entities;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("call_name")
public class CallName {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @TableField("group_id")
    private Long groupId;
    @TableField("user_id")
    private Long userId;
    private String name;
}
