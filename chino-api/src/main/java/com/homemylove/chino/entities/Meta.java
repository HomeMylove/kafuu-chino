package com.homemylove.chino.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.value.qual.ArrayLen;

import java.util.List;


/**
 * 存储元信息
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Meta {

    private Long selfId;

    private Long master;

    private List<Long> superUsers;

}
