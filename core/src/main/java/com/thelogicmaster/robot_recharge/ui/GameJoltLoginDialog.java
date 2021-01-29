package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.thelogicmaster.robot_recharge.RobotRecharge;

public class GameJoltLoginDialog extends RobotDialog {

    public GameJoltLoginDialog(Skin skin) {
        super("Game Jolt Sign In", skin);
        setBackground("windowTen");

        add(new Label("Username", skin)).padBottom(10);
        final TextField userField = new TextField("", skin);
        userField.setMessageText("Username");
        add(userField).padBottom(10).row();

        add(new Label("Token", skin)).padBottom(10);
        final TextField tokenField = new TextField("", skin);
        add(tokenField).padBottom(10).row();

        TextButton closeButton = new PaddedTextButton("Close", skin);
        add(closeButton);

        TextButton loginButton = new PaddedTextButton("Login", skin);
        add(loginButton);

        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (RobotRecharge.gameServices.isSessionActive())
                    return;
                RobotRecharge.prefs.setGameJoltCredentials(userField.getText(), tokenField.getText());
                RobotRecharge.gameServices.setCredentials(userField.getText(), tokenField.getText());
                RobotRecharge.gameServices.logIn();
                setVisible(false);
            }
        });
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setVisible(false);
            }
        });
    }
}
