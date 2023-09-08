package com.homemylove.chino.annotations;


import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

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
