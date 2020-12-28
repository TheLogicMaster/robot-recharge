package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.thelogicmaster.robot_recharge.RobotRecharge;

public class GameMenu extends Window {
    // Todo: switch to modal Window
    public GameMenu(Skin skin, final GameMenuListener listener) {
        super("Menu", skin);
        padTop(100);
        setMovable(false);
        setModal(true);
        setVisible(false);

        final CheckBox gridCheckbox = new CheckBox("Show Grid", skin);
        gridCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                listener.onGridCheckbox(gridCheckbox.isChecked());
            }
        });
        gridCheckbox.setChecked(true);
        add(gridCheckbox).padBottom(20).row();

        TextButton objectivesButton = new TextButton("View Objectives", skin);
        objectivesButton.getLabelCell().pad(0, 10, 0, 10);
        objectivesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setVisible(false);
                listener.onObjectives();
            }
        });
        add(objectivesButton).padBottom(20).row();

        TextButton exitButton = new TextButton("Exit to Main Menu", skin);
        exitButton.getLabelCell().pad(0, 10, 0, 10);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotRecharge.instance.returnToTitle();
                listener.onExit();
            }
        });
        add(exitButton).padBottom(30).row();

        TextButton closeButton = new TextButton("Close", skin);
        closeButton.getLabelCell().pad(0, 10, 0, 10);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setVisible(false);
                listener.onClose();
            }
        });
        add(closeButton).expand().bottom();
    }

    public interface GameMenuListener {

        void onExit();

        void onClose();

        void onGridCheckbox(boolean checked);

        void onObjectives();
    }
}
