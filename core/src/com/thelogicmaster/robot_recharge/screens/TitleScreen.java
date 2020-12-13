package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.thelogicmaster.robot_recharge.RobotRecharge;

public class TitleScreen extends RobotScreen {

    private final LevelScreen levelScreen;
    private final SettingsScreen settingsScreen;

    public TitleScreen() {
        levelScreen = new LevelScreen(this);
        settingsScreen = new SettingsScreen(this);

        Table table = new Table(menuSkin);
        table.setBounds(uiViewport.getWorldWidth() / 2 - 400, 20, 800, uiViewport.getWorldHeight() - 40);
        table.debug(Table.Debug.all);
        table.setBackground("titleMenu");
        TextButton playButton = new TextButton("Play Game", menuSkin);
        table.add(playButton).row();
        TextButton settingsButton = new TextButton("Settings", menuSkin);
        table.add(settingsButton).row();
        TextButton exitButton = new TextButton("Quit Game", menuSkin);
        table.add(exitButton).row();
        stage.addActor(table);
        setBackground(new Texture("titleBackground.png"));

        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotRecharge.instance.setScreen(settingsScreen);
            }
        });
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotRecharge.instance.setScreen(levelScreen);
            }
        });
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        settingsScreen.dispose();
        levelScreen.dispose();
    }
}
