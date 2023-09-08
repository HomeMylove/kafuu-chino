package com.homemylove.chino.annotations;

import com.homemylove.chino.enums.EVENT_TYPE;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Event {
    EVENT_TYPE value() default EVENT_TYPE.POKE;
}
