package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.thelogicmaster.robot_recharge.RobotRecharge;
import com.thelogicmaster.robot_recharge.RobotUtils;
import com.thelogicmaster.robot_recharge.ui.IterativeStack;
import com.thelogicmaster.robot_recharge.ui.PaddedTextButton;

public class TitleScreen extends RobotScreen {

    private final LevelScreen levelScreen;
    private final SettingsScreen settingsScreen;
    private final TutorialsScreen tutorialsScreen;
    private final CloudScreen cloudScreen;
    private final IterativeStack googleSignInStack;

    public TitleScreen() {
        setBackground(new Texture("titleScreen.png"));
        levelScreen = new LevelScreen(this);
        settingsScreen = new SettingsScreen(this);
        tutorialsScreen = new TutorialsScreen(this);
        cloudScreen = new CloudScreen(this);

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

        ImageButton cloudButton = new ImageButton(skin, "cloudSave");
        cloudButton.setPosition(uiViewport.getWorldWidth() - 138, uiViewport.getWorldHeight() - 138);
        cloudButton.pack();
        stage.addActor(cloudButton);

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            googleSignInStack = new IterativeStack();
            Button signInButton = new Button(skin, "googleSignIn");
            googleSignInStack.add(signInButton);
            signInButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    RobotRecharge.gameServices.logIn();
                }
            });
            Button signOutButton = new Button(skin, "googleSignOut");
            signOutButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    RobotRecharge.gameServices.logOff();
                }
            });
            googleSignInStack.add(signOutButton);
            googleSignInStack.setPosition(10, 10);
            googleSignInStack.pack();
            stage.addActor(googleSignInStack);
        } else
            googleSignInStack = null;

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
        cloudButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                RobotRecharge.instance.setScreen(cloudScreen);
            }
        });
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (googleSignInStack != null)
            googleSignInStack.show(RobotRecharge.gameServices.isSessionActive() ? 1 : 0);
    }

    @Override
    public void dispose() {
        super.dispose();
        settingsScreen.dispose();
        levelScreen.dispose();
        tutorialsScreen.dispose();
        cloudScreen.dispose();
    }
}
