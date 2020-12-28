package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.thelogicmaster.robot_recharge.RobotRecharge;
import com.thelogicmaster.robot_recharge.WindowMode;

public class SettingsScreen extends MenuScreen {

    public SettingsScreen(RobotScreen previousScreen) {
        super(previousScreen);

        Table settingsTable = new Table(skin);
        settingsTable.setBackground("windowTen");
        settingsTable.setBounds(uiViewport.getWorldWidth() / 2 - 300, uiViewport.getWorldHeight() / 2 - 400, 600, 800);
        settingsTable.pad(30, 10, 30, 10);
        settingsTable.add(new Label("Settings", skin, "large")).padBottom(30).growX().row();

        if (RobotRecharge.platformUtils.getWindowModes().size > 0) {
            final SelectBox<WindowMode> windowModeSelect = new SelectBox<>(skin);
            windowModeSelect.setItems(RobotRecharge.platformUtils.getWindowModes());
            // Todo: Use preferences
            windowModeSelect.setSelected(WindowMode.Windowed);
            windowModeSelect.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    RobotRecharge.platformUtils.setWindowMode(windowModeSelect.getSelected());
                }
            });
            windowModeSelect.setBounds(50, 50, 200, 30);
            settingsTable.add(windowModeSelect).padBottom(10).growX().row();
        }

        stage.addActor(settingsTable);
    }
}
