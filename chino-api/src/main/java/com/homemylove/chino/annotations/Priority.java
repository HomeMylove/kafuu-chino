package com.homemylove.chino.annotations;


import com.homemylove.chino.enums.PRIORITY;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Indexed;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.annotation.*;

/**
 * 优先级
 * 标注之后会额外提供一系列优先级
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Priority {
    PRIORITY value() default PRIORITY.NOT_IMPORTANT;

}

