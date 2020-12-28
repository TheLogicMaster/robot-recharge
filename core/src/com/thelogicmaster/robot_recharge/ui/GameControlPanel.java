package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.thelogicmaster.robot_recharge.RobotUtils;

public class GameControlPanel extends Table {

    private final ImageButton pauseButton, playButton, programButton;
    private final IterativeStack playPause;

    public GameControlPanel(Skin skin, final ControlPanelListener listener) {
        super(skin);
        top();
        left();

        programButton = new ImageButton(skin, "programmingGame");
        programButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                listener.onProgram();
            }
        });
        add(programButton).padRight(10);

        playButton = new ImageButton(skin, "playGame");
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                playPause.next();
                programButton.setDisabled(true);
                listener.onPlay();
            }
        });
        pauseButton = new ImageButton(skin, "pauseGame");
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                listener.onPause();
            }
        });
        playPause = new IterativeStack(playButton, pauseButton);
        add(playPause).padRight(10);

        ImageButton resetButton = new ImageButton(skin, "resetGame");
        resetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                playPause.show(0);
                programButton.setDisabled(false);
                listener.onReset();
            }
        });
        add(resetButton);

        Table rightTable = new Table();
        rightTable.right().top();

        ImageButton fastForwardButton = new ImageButton(skin, "fastForwardGame");
        fastForwardButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                listener.onFastForward();
            }
        });
        rightTable.add(fastForwardButton);

        ImageButton settingsButton = new ImageButton(skin, "settingsGame");
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                listener.onSettings();
            }
        });
        rightTable.add(settingsButton).right().padLeft(10);

        add(rightTable).grow();
    }

    public void disablePlay() {
        playButton.setDisabled(true);
        pauseButton.setDisabled(true);
        programButton.setDisabled(false);
    }

    public void pause() {
        playPause.show(0);
    }

    public interface ControlPanelListener {

        void onPlay();

        void onPause();

        void onReset();

        void onProgram();

        void onFastForward();

        void onSettings();
    }
}
