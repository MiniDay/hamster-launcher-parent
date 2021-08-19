package cn.hamster3.application.launcher.server;

import cn.hamster3.application.launcher.server.controller.ClientController;
import cn.hamster3.application.launcher.server.controller.LauncherController;
import cn.hamster3.application.launcher.server.controller.MainController;
import cn.hamster3.application.launcher.server.core.Resources;
import cn.hamster3.application.launcher.server.util.RouteUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerBootstrap extends AbstractVerticle {
    public static final Logger logger = LoggerFactory.getLogger("ServerBootstrap");

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        logger.info("已创建 Vertx.");
        vertx.deployVerticle(new ServerBootstrap())
                .onComplete(event -> {
                            if (event.succeeded()) {
                                logger.info("已部署 Verticle.");
                            } else {
                                logger.error("部署 Verticle 失败: ", event.cause());
                                vertx.close();
                            }
                        }
                );
    }

    @Override
    public void start(Promise<Void> startPromise) {
        vertx.createHttpServer()
                .requestHandler(RouteUtils.getRouter(
                        vertx,
                        MainController.class,
                        LauncherController.class,
                        ClientController.class
                )).listen(8888)
                .onComplete(future -> {
                    if (future.succeeded()) {
                        startPromise.complete();
                        logger.info("服务器已在端口 " + future.result().actualPort() + " 上启动.");
                        timerReload();
                        vertx.setPeriodic(1000 * 60, event -> timerReload());
                    } else {
                        startPromise.fail(future.cause());
                        logger.error("服务器启动失败.", future.cause());
                    }
                });
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        vertx.close()
                .onComplete(event -> {
                    if (event.succeeded()) {
                        stopPromise.complete();
                        logger.info("服务器关闭成功.");
                    } else {
                        stopPromise.fail(event.cause());
                        logger.info("服务器关闭失败.");
                    }
                });
    }

    private void timerReload() {
        Resources.getInstance().reloadLauncherInfoList();
    }
}
