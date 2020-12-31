package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * A TextButton with a 20 pixel side padding for the text
 */
public class PaddedTextButton extends TextButton {

    public PaddedTextButton(String text, Skin skin) {
        super(text, skin);
        setup();
    }

    public PaddedTextButton(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
        setup();
    }

    private void setup() {
        getLabelCell().pad(0, 20, 0, 20);
    }
}
