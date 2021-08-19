package cn.hamster3.application.launcher.server.controller;

import cn.hamster3.application.launcher.server.annotation.Route;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class MainController {

    @Route("/")
    public void mainPage(RoutingContext context) {
        context.response()
                .setChunked(true)
                .putHeader("Content-Type", "text/plain")
                .end("HamsterLauncher!");
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
