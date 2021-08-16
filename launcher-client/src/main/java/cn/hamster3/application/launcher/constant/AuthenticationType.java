package cn.hamster3.application.launcher.constant;

import cn.hamster3.application.launcher.entity.auth.AccountProfile;
import cn.hamster3.application.launcher.util.LauncherUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SuppressWarnings("SpellCheckingInspection")
public enum AuthenticationType {
    AIRGAME(
            "五彩方块",
            "https://www.57block.cn",
            "https://www.57block.cn/api/yggdrasil/authserver"
    ),
    OFFICIAL(
            "MOJANG",
            "https://api.mojang.com",
            "https://authserver.mojang.com",
            "https://api.mojang.com",
            "https://sessionserver.mojang.com",
            "https://api.minecraftservices.com"
    );

    private final String name;
    private final String url;
    private final String authHost;
    private final String accountsHost;
    private final String sessionHost;
    private final String servicesHost;

    AuthenticationType(String name, String url, String authHost) {
        this.name = name;
        this.url = url;
        this.authHost = authHost;
        this.accountsHost = null;
        this.sessionHost = null;
        this.servicesHost = null;
    }

    AuthenticationType(String name, String url, String authHost, String accountsHost, String sessionHost, String servicesHost) {
        this.name = name;
        this.url = url;
        this.authHost = authHost;
        this.accountsHost = accountsHost;
        this.sessionHost = sessionHost;
        this.servicesHost = servicesHost;
    }

    public HttpsURLConnection getConnection(String connectionUrl) throws IOException {
        URL url = new URL(connectionUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "utf-8");
        connection.setConnectTimeout(5000);
        return connection;
    }

    private JsonElement post(String apiUrl, String params) throws IOException {
        System.out.println("HTTP POST 请求: " + apiUrl);
        URL url = new URL(apiUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "utf-8");
        connection.setConnectTimeout(5000);
        connection.setRequestMethod("POST");
        System.out.println(params);
        connection.getOutputStream().write(params.getBytes(StandardCharsets.UTF_8));
        System.out.println("请求返回: " + connection.getResponseCode());
        JsonElement reader = JsonParser.parseReader(new InputStreamReader(connection.getInputStream()));
        connection.getInputStream().close();
        System.out.println(LauncherUtils.gson.toJson(reader));
        System.out.println();
        return reader;
    }

    private JsonElement get(String apiUrl) throws IOException {
        System.out.println("HTTP GET 请求: " + apiUrl);
        URL url = new URL(apiUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setRequestProperty("charset", "utf-8");
        connection.setConnectTimeout(5000);
        connection.setRequestMethod("GET");
        System.out.println("请求返回: " + connection.getResponseCode());
        JsonElement reader = JsonParser.parseReader(new InputStreamReader(connection.getInputStream()));
        connection.getInputStream().close();
        System.out.println(LauncherUtils.gson.toJson(reader));
        System.out.println();
        return reader;
    }

    /**
     * https://github.com/yushijinhun/authlib-injector/wiki/Yggdrasil-%E6%9C%8D%E5%8A%A1%E7%AB%AF%E6%8A%80%E6%9C%AF%E8%A7%84%E8%8C%83#%E7%99%BB%E5%BD%95
     */
    public JsonObject postLogin(String account, String password, boolean requestUser) throws IOException {
        JsonObject object = new JsonObject();
        object.addProperty("username", account);
        object.addProperty("password", password);
        object.addProperty("requestUser", requestUser);

        JsonObject agent = new JsonObject();
        agent.addProperty("name", "Minecraft");
        agent.addProperty("version", 1);
        object.add("agent", agent);

        return post(authHost + "/authenticate", object.toString()).getAsJsonObject();
    }

    public JsonObject postRefresh(AccountProfile profile, boolean requestUser) throws IOException {
        JsonObject object = new JsonObject();
        object.addProperty("name", profile.getPlayerName());
        object.addProperty("id", profile.getPlayerUUID());
        return postRefresh(
                profile.getAccessToken(),
                profile.getClientToken(),
                requestUser,
                object
        ).getAsJsonObject();
    }

    /**
     * https://github.com/yushijinhun/authlib-injector/wiki/Yggdrasil-%E6%9C%8D%E5%8A%A1%E7%AB%AF%E6%8A%80%E6%9C%AF%E8%A7%84%E8%8C%83#%E5%88%B7%E6%96%B0
     */
    public JsonElement postRefresh(String accessToken, String clientToken, boolean requestUser, JsonObject selectedProfile) throws IOException {
        JsonObject object = new JsonObject();
        object.addProperty("accessToken", accessToken);
        object.addProperty("clientToken", clientToken);
        object.addProperty("requestUser", requestUser);
        object.add("selectedProfile", selectedProfile);

        return post(authHost + "/refresh", object.toString());
    }

    public boolean postValidate(AccountProfile profile) throws IOException {
        JsonObject object = new JsonObject();
        object.addProperty("accessToken", profile.getAccessToken());
        object.addProperty("clientToken", profile.getClientToken());
        String params = object.toString();
        String apiUrl = authHost + "/validate";
        System.out.println("HTTP POST 请求: " + apiUrl);
        HttpsURLConnection connection = getConnection(apiUrl);
        connection.setRequestMethod("POST");
        System.out.println(params);
        connection.getOutputStream().write(params.getBytes(StandardCharsets.UTF_8));
        int code = connection.getResponseCode();
        System.out.println("请求返回: " + code);
        System.out.println();
        return code == 204;
    }

    public String getSkilUrl(String playerUUID, String playerName) {
        switch (this) {
            case AIRGAME: {
                return url + "/skin/" + playerName + ".png";
            }
            case OFFICIAL: {
                try {
                    JsonObject object = get(sessionHost + "/session/minecraft/profile/" + playerUUID).getAsJsonObject();
                    if (!object.has("properties")) {
                        return null;
                    }
                    for (JsonElement element : object.getAsJsonArray("properties")) {
                        JsonObject propetrie = element.getAsJsonObject();
                        if (!propetrie.get("name").getAsString().equalsIgnoreCase("textures")) {
                            continue;
                        }
                        String value = propetrie.get("value").getAsString();
                        value = new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
                        JsonObject textureObject = JsonParser.parseString(value).getAsJsonObject();
                        return textureObject.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getAuthHost() {
        return authHost;
    }

    public String getAccountsHost() {
        return accountsHost;
    }

    public String getSessionHost() {
        return sessionHost;
    }

    public String getServicesHost() {
        return servicesHost;
    }

    @Override
    public String toString() {
        return name;
    }
}