package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Base64Coder;
import com.thelogicmaster.robot_recharge.RobotAssets;
import com.thelogicmaster.robot_recharge.RobotRecharge;
import com.thelogicmaster.robot_recharge.RobotUtils;
import com.thelogicmaster.robot_recharge.ui.GameJoltLoginDialog;
import com.thelogicmaster.robot_recharge.ui.IterativeStack;
import com.thelogicmaster.robot_recharge.ui.PaddedTextButton;
import de.golfgl.gdxgamesvcs.IGameServiceClient;

public class CloudScreen extends MenuScreen {

    private final Label userLabel;
    private final TextButton loadButton, saveButton;
    private final GameJoltLoginDialog loginDialog;
    private final IterativeStack loginStack;

    public CloudScreen(RobotScreen previousScreen) {
        super(previousScreen);

        Table cloudTable = new Table(skin);
        cloudTable.setBackground("secondaryPanel");
        cloudTable.setBounds(uiViewport.getWorldWidth() / 2 - 700, uiViewport.getWorldHeight() / 2 - 400, 1400, 800);
        cloudTable.pad(30);

        userLabel = new Label("Not Connected", skin);
        cloudTable.add(userLabel).padBottom(20).row();

        loginStack = new IterativeStack();
        TextButton loginButton = new PaddedTextButton("Login", skin);
        loginStack.add(loginButton);
        TextButton logoutButton = new PaddedTextButton("Logout", skin);
        loginStack.add(logoutButton);
        logoutButton.setDisabled(!RobotRecharge.gameServices.isFeatureSupported(IGameServiceClient.GameServiceFeature.PlayerLogOut));
        cloudTable.add(loginStack).padBottom(10).row();

        loadButton = new PaddedTextButton("Load Cloud Save", skin);
        cloudTable.add(loadButton).padBottom(10).row();

        saveButton = new PaddedTextButton("Create Cloud Save", skin);
        cloudTable.add(saveButton).padBottom(10).row();

        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.loadCloudSave();
            }
        });
        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Encode save to base64 to ensure that blockly xml data doesn't break everything
                RobotRecharge.gameServices.saveGameState("save",
                        Base64Coder.encodeString(RobotAssets.json.toJson(RobotRecharge.prefs.getGameSave())).getBytes(),
                        RobotRecharge.prefs.getUnlockedLevel(), (success, errorCode) -> {
                        });
            }
        });
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (RobotUtils.usesGameJolt())
                    loginDialog.show(stage);
                else
                    RobotRecharge.gameServices.logIn();
            }
        });
        logoutButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotRecharge.gameServices.logOff();
                RobotRecharge.prefs.clearGameJoltCredentials();
            }
        });

        stage.addActor(cloudTable);

        loginDialog = new GameJoltLoginDialog();
    }

    @Override
    public void render(float delta) {
        boolean signedIn = RobotRecharge.gameServices.isSessionActive();
        userLabel.setText(signedIn ? "Username: " + RobotRecharge.gameServices.getPlayerDisplayName() : "Not Connected");
        saveButton.setDisabled(!signedIn);
        loadButton.setDisabled(!signedIn);
        loginStack.show(signedIn ? 1 : 0);
        super.render(delta);
    }
}
