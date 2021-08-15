package cn.hamster3.application.launcher.server;

import cn.hamster3.application.launcher.server.controller.MainController;
import cn.hamster3.application.launcher.server.util.RouteUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class Bootstrap extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.createHttpServer()
                .requestHandler(RouteUtils.getRouter(vertx, new MainController()))
                .listen(8888)
                .onComplete(http -> {
                    if (http.succeeded()) {
                        startPromise.complete();
                        System.out.println("HTTP server started on port 8888");
                    } else {
                        startPromise.fail(http.cause());
                    }
                });
    }
}
