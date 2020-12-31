package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.LevelInfo;
import com.thelogicmaster.robot_recharge.LevelSave;
import com.thelogicmaster.robot_recharge.RobotRecharge;
import com.thelogicmaster.robot_recharge.RobotUtils;
import com.thelogicmaster.robot_recharge.screens.GameScreen;

public class LevelCompleteDialog extends RobotDialog {

    private final Label completionTime, completionLength, completionCalls;
    private final boolean useBlocks;

    public LevelCompleteDialog(Skin skin, final LevelSave level, final LevelCompleteListener listener) {
        super("Level Complete", skin);
        useBlocks = level.usingBlocks();

        add(completionTime = new Label("", skin, "small")).left().row();
        add(completionLength = new Label("", skin, "small")).left().row();
        add(completionCalls = new Label("", skin, "small")).left().row();

        TextButton closeButton = new TextButton("Close", skin);
        Table buttonTable = new Table(skin);
        buttonTable.add(closeButton).expandX().left();

        TextButton exitButton = new TextButton("Exit", skin);
        buttonTable.add(exitButton).expandX();

        Array<LevelInfo> levels = RobotRecharge.assets.levelInfo;
        int index = -1;
        for (int i = 0; i < levels.size; i++)
            if (levels.get(i).getName().equals(level.getLevel())) {
                index = i + 1;
                break;
            }
        final String next = index >= levels.size || index == -1 ? null : levels.get(index).getName();
        TextButton nextButton = new TextButton("Next Level", skin);
        nextButton.setDisabled(next == null);
        buttonTable.add(nextButton).expandX().right();
        add(buttonTable).bottom().expand().fillX();

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                setVisible(false);
            }
        });
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                listener.onDispose();
                RobotRecharge.instance.returnToTitle();
            }
        });
        nextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                listener.onDispose();
                RobotRecharge.instance.setScreen(new GameScreen(new LevelSave(next, level.usingBlocks(), "", level.getLanguage())));
            }
        });
    }

    public void show(float completionTime, int length, int calls) {
        setVisible(true);
        this.completionTime.setText("Completion Time: " + ((int) completionTime) + " seconds");
        this.completionLength.setText("Robot " + (useBlocks ? "Blocks: " : "References: ") + length);
        this.completionCalls.setText("Robot Function Calls: " + calls);
    }

    public interface LevelCompleteListener {

        void onDispose();
    }
}
