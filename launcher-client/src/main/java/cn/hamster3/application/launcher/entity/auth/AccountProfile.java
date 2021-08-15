package cn.hamster3.application.launcher.entity.auth;

import cn.hamster3.application.launcher.constant.AuthenticationType;
import javafx.scene.image.Image;
import cn.hamster3.application.launcher.util.LauncherUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;

public class AccountProfile {
    private final AuthenticationType type;
    private final String account;
    private final String playerUUID;

    private String playerName;
    private String accessToken;
    private String clientToken;

    private String playerIcon;
    private long lastUpdateIconTime;

    public AccountProfile(AuthenticationType type, String account, String playerName, String playerUUID, String accessToken, String clientToken) {
        this.type = type;
        this.account = account;
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.accessToken = accessToken;
        this.clientToken = clientToken;
    }

    public AuthenticationType getType() {
        return type;
    }

    public String getAccount() {
        return account;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }

    public String getPlayerIcon() {
        return playerIcon;
    }

    public Image getPlayerIconImage() {
        ByteArrayInputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(getPlayerIcon()));
        Image image = new Image(stream);
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void refreshPlayerIcon() {
        try {
            String skinUrl = type.getSkilUrl(playerUUID, playerName);
            if (skinUrl == null) {
                return;
            }
            URL url = new URL(skinUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            playerIcon = Base64.getEncoder().encodeToString(LauncherUtils.getAvatorFromSkin(inputStream, 128));
            lastUpdateIconTime = System.currentTimeMillis();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean shouldRefreshIcon() {
        // 10分钟缓存超时
        return System.currentTimeMillis() - lastUpdateIconTime >= 1000 * 60 * 10;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountProfile account)) return false;
        return type == account.type && playerUUID.equals(account.playerUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, playerUUID);
    }

    @Override
    public String toString() {
        return "Account{" +
                "type=" + type +
                ", account='" + account + '\'' +
                ", playerName='" + playerName + '\'' +
                ", playerUUID='" + playerUUID + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", clientToken='" + clientToken + '\'' +
                ", playerIcon='" + playerIcon + '\'' +
                '}';
    }
}
