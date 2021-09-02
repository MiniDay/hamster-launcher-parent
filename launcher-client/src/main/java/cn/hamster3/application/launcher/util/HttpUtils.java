package cn.hamster3.application.launcher.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public abstract class HttpUtils {

    public static JsonElement get(String url) throws IOException {
        System.out.println("HTTP GET 请求: " + url);
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setRequestProperty("charset", "utf-8");
        connection.setConnectTimeout(5000);
        connection.setRequestMethod("GET");
        connection.connect();
        System.out.println("请求返回: " + connection.getResponseCode());
        JsonElement reader = JsonParser.parseReader(new InputStreamReader(connection.getInputStream()));
        connection.getInputStream().close();
        connection.disconnect();
        System.out.println("返回内容: " + reader.toString());
        return reader;
    }

    public static JsonElement post(String url, String params) throws IOException {
        System.out.println("HTTP POST 请求: " + url);
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "utf-8");
        connection.setConnectTimeout(5000);
        connection.setRequestMethod("POST");
        System.out.println("请求参数: " + params);
        connection.getOutputStream().write(params.getBytes(StandardCharsets.UTF_8));
        connection.connect();
        System.out.println("请求返回: " + connection.getResponseCode());
        JsonElement reader = JsonParser.parseReader(new InputStreamReader(connection.getInputStream()));
        connection.getInputStream().close();
        connection.disconnect();
        System.out.println("返回内容: " + reader.toString());
        return reader;
    }
}
