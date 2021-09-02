package cn.hamster3.application.launcher.thread;

import cn.hamster3.application.launcher.util.HttpUtils;
import com.google.gson.JsonObject;

import java.io.IOException;

public class UpdateCheckThread extends Thread {
    public static final String baseURl = "http://localhost:8888";

    @Override
    public void run() {
        try {
            //todo
            HttpUtils.get(baseURl + "/version/launcher/latest");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
