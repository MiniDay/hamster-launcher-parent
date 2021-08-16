package cn.hamster3.application.launcher.server.util;

import cn.hamster3.application.launcher.server.annotation.Route;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class RouteUtils {
    public static final Logger logger = LoggerFactory.getLogger("Router");

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
            HttpMethod httpMethod = getMethod(annotation);
            String routePath = annotation.value();
            io.vertx.ext.web.Route route;
            if (annotation.isRegex()) {
                route = router.routeWithRegex(httpMethod, routePath);
            } else {
                route = router.route(httpMethod, routePath);
            }
            route.handler(context -> {
                try {
                    HttpServerRequest request = context.request();
                    logger.info("{} 访问 {}", request.remoteAddress(), request.path());
                    method.invoke(controller, context);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.error(String.format("处理 %s 时遇到了一个异常: ", routePath), e);
                }
            });
            logger.info("已创建路由: {} ", routePath);
        }
        return router;
    }

    private static HttpMethod getMethod(Route route) {
        if (route.method().length < 1) {
            return HttpMethod.GET;
        }
        return HttpMethod.valueOf(route.method()[0]);
    }
}
