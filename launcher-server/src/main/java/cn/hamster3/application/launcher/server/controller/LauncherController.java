package cn.hamster3.application.launcher.server.controller;

import cn.hamster3.application.launcher.server.annotation.Route;
import cn.hamster3.application.launcher.server.core.Resources;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.io.File;

public class LauncherController {

    @Route("/version/launcher")
    public static void getLauncherVersionInfoList(RoutingContext context) {
        context.response()
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .end(Resources.getInstance().getLauncherInfoArray().toString());
    }

    @Route("/version/launcher/*")
    public static void getLauncherVersionInfo(RoutingContext context) {
        String version = context.request().uri();
        version = version.substring(version.lastIndexOf('/') + 1);
        JsonObject info = Resources.getInstance().getLauncherInfo(version);
        if (info == null) {
            context.response()
                    .setStatusCode(404)
                    .putHeader("Content-Type", "application/json; charset=utf-8")
                    .end(new JsonObject()
                            .put("error", "未找到该版本!")
                            .toString());
            return;
        }
        context.response()
                .putHeader("Content-Type", "application/json; charset=utf-8")
                .end(info.toString());
    }

    @Route("/download/launcher/*")
    public static void downloadLauncher(RoutingContext context) {
        System.out.println("/download/launcher/*");
        String version = context.request().uri();
        version = version.substring(version.lastIndexOf('/') + 1);
        File file = Resources.getInstance().getLauncherFile(version);
        if (!file.exists()) {
            context.response()
                    .setStatusCode(404)
                    .putHeader("Content-Type", "application/json; charset=utf-8")
                    .end(new JsonObject()
                            .put("error", "未找到该版本!")
                            .toString());
            return;
        }
        System.out.println("file found");
        context.response()
                .putHeader("Content-Disposition", "filename=" + file.getName())
                .sendFile(file.getAbsolutePath());
    }
}
