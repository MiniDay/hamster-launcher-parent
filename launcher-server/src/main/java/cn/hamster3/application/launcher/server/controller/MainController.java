package cn.hamster3.application.launcher.server.controller;

import cn.hamster3.application.launcher.server.annotation.Route;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainController {
    public static final Logger logger = LoggerFactory.getLogger("MainController");

    @Route("/")
    public void mainPage(RoutingContext context) {
        context.response()
                .setChunked(true)
                .putHeader("Content-Type", "text/plain")
                .end("HamsterLauncher!");
    }

    @Route("/latestVersion/launcher")
    public void getLauncherLatestVersion(RoutingContext context) {
        context.response()
                .setChunked(true)
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .end(new JsonObject()
                        .put("version", "1.0.0-SNAPSHOT")
                        .toString()
                );
    }

    @Route("/latestVersion/client")
    public void getClientLatestVersion(RoutingContext context) {
        context.response()
                .setChunked(true)
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .end(new JsonObject()
                        .put("version", 1)
                        .toString()
                );
    }

    @Route("/download")
    public void downloadLauncher(RoutingContext context) {
        context.response().sendFile("HamsterLauncher-1.0.0-SNAPSHOT.jar");
    }

    @Route("/assets")
    public void downloadAssets(RoutingContext context) {
        context.response()
                .setChunked(true)
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .end(new JsonObject()
                        .put("error", "not support")
                        .toString()
                );
    }

    @Route("/libraries/*")
    public void downloadLibraries(RoutingContext context) {
        context.response()
                .setChunked(true)
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .end(new JsonObject()
                        .put("error", "not support")
                        .toString()
                );
    }

    @Route("/mods/*")
    public void downloadMods(RoutingContext context) {
        context.response()
                .setChunked(true)
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .end(new JsonObject()
                        .put("error", "not support")
                        .toString()
                );
    }
}
