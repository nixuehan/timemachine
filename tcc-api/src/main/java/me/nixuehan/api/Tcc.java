package me.nixuehan.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Tcc {

    /**
     * 确认事务的方法
     *
     * @return
     */
    String confirmMethod() default "";

    /**
     * 取消事务的方法
     * @return
     */
    String cancelMethod() default "";

    /**
     * 是否异步
     */
    boolean async() default true;
}
