package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.thelogicmaster.robot_recharge.RobotRecharge;
import com.thelogicmaster.robot_recharge.RobotUtils;
import com.thelogicmaster.robot_recharge.ui.PaddedTextButton;

public class TitleScreen extends RobotScreen {

    private final LevelScreen levelScreen;
    private final SettingsScreen settingsScreen;
    private final TutorialsScreen tutorialsScreen;

    public TitleScreen() {
        setBackground(new Texture("titleScreen.png"));
        levelScreen = new LevelScreen(this);
        settingsScreen = new SettingsScreen(this);
        tutorialsScreen = new TutorialsScreen(this);

        Table table = new Table(skin);
        table.setBounds(uiViewport.getWorldWidth() / 2 - 400, 20, 800, uiViewport.getWorldHeight() - 40);
        table.setBackground("titleMenu");
        table.add(new Label("Robot Recharge", skin, "large")).padBottom(100).row();
        TextButton playButton = new PaddedTextButton("Play Game", skin, "large");
        table.add(playButton).padBottom(20).fillX().row();
        TextButton tutorialsButton = new PaddedTextButton("Tutorials", skin, "large");
        table.add(tutorialsButton).padBottom(20).fillX().row();
        TextButton settingsButton = new PaddedTextButton("Settings", skin, "large");
        table.add(settingsButton).padBottom(120).fillX().row();
        TextButton exitButton = new PaddedTextButton("Quit Game", skin, "large");
        table.add(exitButton);
        stage.addActor(table);

        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                RobotRecharge.instance.setScreen(settingsScreen);
            }
        });
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                RobotRecharge.instance.setScreen(levelScreen);
            }
        });
        tutorialsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                RobotRecharge.instance.setScreen(tutorialsScreen);
            }
        });
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
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
