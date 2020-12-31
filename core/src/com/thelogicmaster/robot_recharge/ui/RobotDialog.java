package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

/**
 * A modal Window that is padded correctly and isn't movable
 */
public class RobotDialog extends Window {

    public RobotDialog(String title, Skin skin) {
        super(title, skin);
        setup();
    }

    public RobotDialog(String title, Skin skin, String styleName) {
        super(title, skin, styleName);
        setup();
    }

    private void setup() {
        setModal(true);
        setMovable(false);
        setVisible(false);
        padTop(100);
    }
}
