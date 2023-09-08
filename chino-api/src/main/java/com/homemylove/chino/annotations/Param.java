package com.homemylove.chino.annotations;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * 结合 @Match(regex) 使用
 * 在 regex 中使用 {content} 标注的内容可以直接通过 @Param("context") 取出
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Param {
    String value() default "";
}
