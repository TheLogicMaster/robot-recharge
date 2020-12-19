package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;

public class CodeArea extends TextArea {


    public CodeArea(Skin skin) {
        super("", skin);
    }

    public CodeArea(Skin skin, String styleName) {
        super("", skin, styleName);
    }

    public void updateLines() {
        calculateOffsets();
    }
}
