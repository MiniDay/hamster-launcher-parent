package cn.hamster3.application.launcher.server.util;

import cn.hamster3.application.launcher.server.annotation.Route;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class RouteUtils {
    public static Router getRouter(Vertx vertx, Object controller) {
        Router router = Router.router(vertx);
        Class<?> clazz = controller.getClass();
        for (Method method : clazz.getMethods()) {
            Route annotation = method.getAnnotation(Route.class);
            if (annotation == null) {
                continue;
            }
            if (method.getParameterCount() != 1) {
                continue;
            }
            if (!annotation.method().equals("null")) {
                router.route(HttpMethod.valueOf(annotation.method()), annotation.value())
                        .handler(event -> {
                            try {
                                method.invoke(null, event);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        });
            } else {
                router.route(annotation.value())
                        .handler(event -> {
                            try {
                                method.invoke(controller, event);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        });
            }
        }
        return router;
    }
}
