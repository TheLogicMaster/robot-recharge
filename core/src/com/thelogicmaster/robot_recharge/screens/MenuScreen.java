package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.thelogicmaster.robot_recharge.RobotRecharge;

public abstract class MenuScreen extends RobotScreen {

    private RobotScreen previousScreen;

    public MenuScreen(RobotScreen previousScreen) {
        this.previousScreen = previousScreen;
        createBackButton();
    }

    protected void createBackButton() {
        ImageButton backButton = new ImageButton(menuSkin, "backButton");
        backButton.setBounds(20, 20, 100, 100);
        stage.addActor(backButton);

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showPreviousScreen();
            }
        });
    }

    protected void showPreviousScreen() {
        RobotRecharge.instance.setScreen(previousScreen);
    }
}
