package com.homemylove.chino.annotations;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;


/**
 * 没有任何特殊的功能
 * 当一个方法标注了@Anyway，它一定会校验通过（但是不一定能进入校验）
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Anyway {
}
