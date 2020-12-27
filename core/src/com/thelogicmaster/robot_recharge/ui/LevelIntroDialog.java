package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class LevelIntroDialog extends Dialog {

    public LevelIntroDialog(Skin skin, String level) {
        super(level, skin);
        padTop(50);
        TextButton introCloseButton = new TextButton("Close", skin);
        getContentTable().add(new Label("", skin));
        getButtonTable().add(introCloseButton);
        introCloseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
            }
        });
    }
}
