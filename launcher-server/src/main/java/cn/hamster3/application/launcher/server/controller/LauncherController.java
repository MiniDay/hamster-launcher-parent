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

    @Route("/version/launcher/latest")
    public static void getLatestLauncherVersionInfo(RoutingContext context) {
        JsonObject info = Resources.getInstance().getLatestLauncherInfo();
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
        String version = context.request().uri();
        version = version.substring(version.lastIndexOf('/') + 1);
        File file = Resources.getInstance().getLauncherFile(version);
        if (file == null) {
            context.response()
                    .setStatusCode(404)
                    .putHeader("Content-Type", "application/json; charset=utf-8")
                    .end(new JsonObject()
                            .put("error", "未找到该版本!")
                            .toString());
            return;
        }
        context.response()
                .sendFile(file.getAbsolutePath()).onComplete(event -> context.end());
    }

    @Route("/download/launcher/latest/")
    public static void downloadLatestLauncher(RoutingContext context) {
        String version = context.request().uri();
        version = version.substring(version.lastIndexOf('/') + 1);
        File file = Resources.getInstance().getLauncherFile(version);
        if (file == null) {
            context.response()
                    .setStatusCode(404)
                    .putHeader("Content-Type", "application/json; charset=utf-8")
                    .end(new JsonObject()
                            .put("error", "未找到该版本!")
                            .toString());
            return;
        }
        context.response()
                .sendFile(file.getAbsolutePath()).onComplete(event -> context.end());
    }
}
