package com.homemylove.chino.annotations;


import com.homemylove.chino.enums.PRIORITY;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Indexed;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Priority {
    PRIORITY value() default PRIORITY.NOT_IMPORTANT;

}

