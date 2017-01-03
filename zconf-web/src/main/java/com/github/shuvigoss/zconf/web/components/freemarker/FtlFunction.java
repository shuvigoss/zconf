package com.github.shuvigoss.zconf.web.components.freemarker;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface FtlFunction {

  String prefix() default "";
}
