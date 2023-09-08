package com.homemylove.chino.annotations;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;


/**
 * 标注一个插件
 * 继承 @Component，将类注入容器并成为插件
 */
@Component
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Plugin {
}
