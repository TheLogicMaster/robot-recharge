package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.VisUI;

public class RobotAssets implements Disposable {

    public final BitmapFont fontNormal, fontLarge;
    public final Skin skin;

    public RobotAssets() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("monog.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.borderWidth = 1;
        parameter.color = Color.YELLOW;
        //parameter.shadowOffsetX = 3;
        //parameter.shadowOffsetY = 3;
        parameter.shadowColor = new Color(0, 0.5f, 0, 0.75f);
        fontNormal = generator.generateFont(parameter);
        parameter.size = 160;
        fontLarge = generator.generateFont(parameter);
        generator.dispose();

        if (!VisUI.isLoaded())
            VisUI.load(VisUI.SkinScale.X2);

        skin = new Skin();
        skin.add("menuFont", fontNormal);
        skin.addRegions(new TextureAtlas("skin.atlas"));
        skin.load(Gdx.files.internal("skin.json"));
    }

    @Override
    public void dispose() {
        fontLarge.dispose();
        fontNormal.dispose();
    }
}
