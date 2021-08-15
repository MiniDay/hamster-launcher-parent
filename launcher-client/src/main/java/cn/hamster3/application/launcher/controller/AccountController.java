package cn.hamster3.application.launcher.controller;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import cn.hamster3.application.launcher.entity.auth.AccountProfile;
import cn.hamster3.application.launcher.entity.option.LaunchOptions;

public class AccountController {
    public ImageView deleteImage;

    public ImageView playerIcon;
    public Label playerName;
    public Label type;
    public Label account;

    private AccountProfile profile;

    public void setAccount(AccountProfile profile) {
        this.profile = profile;
        playerName.setText(profile.getPlayerName());
        type.setText(profile.getType().getName());
        account.setText(profile.getAccount());
        new Thread(() -> {
            if (profile.getPlayerIcon() == null) {
                profile.refreshPlayerIcon();
                playerIcon.setImage(profile.getPlayerIconImage());
            } else {
                playerIcon.setImage(profile.getPlayerIconImage());
                if (profile.shouldRefreshIcon()) {
                    profile.refreshPlayerIcon();
                    playerIcon.setImage(profile.getPlayerIconImage());
                }
            }
        }).start();
    }

    public void onClickProfile(MouseEvent event) {
        if (event.getTarget() == deleteImage) {
            LaunchOptions options = LaunchOptions.getInstance();
            options.removeProfile(profile);
            options.save();
        } else {
            LaunchOptions options = LaunchOptions.getInstance();
            options.setSelectedProfile(profile);
            options.save();
        }
        deleteImage.getScene().getWindow().hide();
    }
}
