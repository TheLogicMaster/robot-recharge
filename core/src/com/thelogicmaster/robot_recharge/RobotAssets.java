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

    public final BitmapFont fontSmall, fontNormal, fontLarge, fontHuge;
    public final Skin skin;

    public RobotAssets() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("monog.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = Color.BLACK;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 1;
        parameter.size = 30;
        fontSmall = generator.generateFont(parameter);
        parameter.size = 50;
        fontNormal = generator.generateFont(parameter);
        parameter.size = 90;
        fontLarge = generator.generateFont(parameter);
        parameter.size = 160;
        fontHuge = generator.generateFont(parameter);
        generator.dispose();

        if (!VisUI.isLoaded())
            VisUI.load(VisUI.SkinScale.X2);

        skin = new Skin();
        skin.add("menuFont", fontSmall);
        skin.add("titleFont", fontLarge);
        skin.addRegions(new TextureAtlas("skin.atlas"));
        skin.load(Gdx.files.internal("skin.json"));
    }

    @Override
    public void dispose() {
        fontLarge.dispose();
        fontNormal.dispose();
    }
}
