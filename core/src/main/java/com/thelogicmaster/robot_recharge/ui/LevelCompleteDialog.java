package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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

    public LevelCompleteDialog(final LevelSave level, final LevelCompleteListener listener) {
        super("Level Complete");
        useBlocks = level.isUsingBlocks();

        add(completionTime = new Label("", getSkin(), "small")).left().row();
        add(completionLength = new Label("", getSkin(), "small")).left().row();
        add(completionCalls = new Label("", getSkin(), "small")).left().row();

        TextButton closeButton = new PaddedTextButton("Close", getSkin());
        Table buttonTable = new Table(getSkin());
        buttonTable.add(closeButton).expandX().left();

        TextButton exitButton = new PaddedTextButton("Exit", getSkin());
        buttonTable.add(exitButton).expandX();

        Array<LevelInfo> levels = RobotRecharge.assets.levelInfo;
        int index = -1;
        for (int i = 0; i < levels.size; i++)
            if (levels.get(i).getName().equals(level.getLevel())) {
                index = i + 1;
                break;
            }
        final String next = index >= levels.size || index == -1 ? null : levels.get(index).getName();
        TextButton nextButton = new PaddedTextButton("Next Level", getSkin());
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
                RobotRecharge.instance.setScreen(new GameScreen(new LevelSave(level.isUsingBlocks(), "", next, level.getLanguage())));
            }
        });
    }

    public void show(Stage stage, float completionTime, int length, int calls) {
        show(stage);

        this.completionTime.setText("Completion Time: " + ((int) completionTime) + " seconds");
        this.completionLength.setText("Robot " + (useBlocks ? "Blocks: " : "References: ") + length);
        this.completionCalls.setText("Robot Function Calls: " + calls);
    }

    public interface LevelCompleteListener {

        void onDispose();
    }
}
