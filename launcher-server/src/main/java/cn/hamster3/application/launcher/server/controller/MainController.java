package cn.hamster3.application.launcher.server.controller;

import cn.hamster3.application.launcher.server.annotation.Route;
import io.vertx.ext.web.RoutingContext;

public class MainController {
    @Route("/version")
    public void getLauncherVersion(RoutingContext event) {
        event.response()
                .putHeader("content-type", "text/plain")
                .send("test version");
    }

}
