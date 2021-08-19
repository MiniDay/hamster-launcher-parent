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

    @SuppressWarnings("deprecation")
    public static Router getRouter(Vertx vertx, Class<?>... classes) {
//        Class<?> clazz = controller.getClass();
        Router router = Router.router(vertx);
        for (Class<?> clazz : classes) {

            Object o = null;
            try {
                o = clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Object controller = o;
            for (Method method : clazz.getMethods()) {
                Route annotation = method.getAnnotation(Route.class);
                if (annotation == null) {
                    continue;
                }
                HttpMethod httpMethod = getMethod(annotation);
                String path = annotation.value();
                io.vertx.ext.web.Route route;
                if (annotation.withRegex()) {
                    route = router.routeWithRegex(httpMethod, path);
                } else {
                    route = router.route(httpMethod, path);
                }
                route.handler(context -> {
                    try {
                        HttpServerRequest request = context.request();
                        logger.info("{} 访问 {}", request.remoteAddress(), request.path());
                        method.invoke(controller, context);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        logger.error(String.format("处理 %s 时遇到了一个异常: ", path), e);
                        context.response().setStatusCode(500);
                        context.end();
                    }
                });
                if (route.isRegexPath()) {
                    logger.info("已创建路由(WithRegex): {} ", path);
                } else {
                    logger.info("已创建路由: {} ", path);
                }
            }
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
