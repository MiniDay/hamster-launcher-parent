package cn.hamster3.application.launcher.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Route {
    String value();

    /**
     * https://vertx.io/docs/vertx-web/java/#_routing_with_regular_expressions
     *
     * @return 是否以正则表达式判断
     */
    boolean withRegex() default false;

    String[] method() default {};
}
