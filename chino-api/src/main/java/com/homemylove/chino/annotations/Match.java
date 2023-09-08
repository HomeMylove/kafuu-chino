package com.homemylove.chino.annotations;


import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * 匹配，提供多种匹配方法
 * startWith：以数组内的内容开头
 * endWith：以数组内的内容结束
 * contains：包含数组内的内容
 * equals：完全匹配内容
 * regex：完全匹配正则
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Match {
    String[] startWith() default {};
    String[] endWith() default {};
    String[] contains() default {};
    String[] equals() default {};

    String type() default "group";

    String regex() default "";

}
