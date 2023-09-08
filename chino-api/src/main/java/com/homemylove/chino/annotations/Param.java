package com.homemylove.chino.annotations;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Param {
    String value() default "";
}
