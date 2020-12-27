package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.thelogicmaster.robot_recharge.RobotRecharge;

public class GameMenu extends Dialog {
    // Todo: switch to modal Window
    public GameMenu(Skin skin, final GameMenuListener listener) {
        super("Menu", skin);
        padTop(90);
        setMovable(false);
        getContentTable().pad(50, 50, 0, 50);

        final CheckBox gridCheckbox = new CheckBox("Show Grid", skin);
        gridCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                listener.onGridCheckbox(gridCheckbox.isChecked());
            }
        });
        gridCheckbox.setChecked(true);
        getContentTable().add(gridCheckbox).padBottom(20).row();

        TextButton exitButton = new TextButton("Exit to Main Menu", skin);
        exitButton.getLabelCell().pad(0, 10, 0, 10);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotRecharge.instance.returnToTitle();
                listener.onExit();
            }
        });
        getContentTable().add(exitButton).row();

        TextButton closeButton = new TextButton("Close", skin);
        closeButton.getLabelCell().pad(0, 10, 0, 10);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
                listener.onClose();
            }
        });
        getContentTable().add(closeButton).padBottom(5);
    }

    public interface GameMenuListener {

        void onExit();

        void onClose();

        void onGridCheckbox(boolean checked);
    }
}
