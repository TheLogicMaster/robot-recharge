package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
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
