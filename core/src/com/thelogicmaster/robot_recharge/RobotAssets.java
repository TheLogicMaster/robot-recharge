package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.kotcrab.vis.ui.VisUI;

public class RobotAssets implements Disposable {

    public static final Json json = RobotUtils.createJson();

    public final BitmapFont fontSmall, fontNormal, fontLarge, fontHuge;
    public final Skin skin;
    public final Array<LevelInfo> levelInfo;
    public final Music titleMusic;
    public final Sound navigateSound;

    private final AssetManager assets = RobotUtils.createAssetManager();

    public RobotAssets() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("monog.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = Color.WHITE;
        parameter.borderColor = Color.WHITE;
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

        skin = new Skin();
        skin.add("infoFont", fontSmall);
        skin.add("menuFont", fontNormal);
        skin.add("titleFont", fontLarge);
        skin.add("hugeFont", fontHuge);
        skin.addRegions(new TextureAtlas("skin.atlas"));
        skin.load(Gdx.files.internal("skin.json"));
        skin.get("default-horizontal", Slider.SliderStyle.class).knob.setMinHeight(50);

        if (!VisUI.isLoaded()) {
            VisUI.load(VisUI.SkinScale.X2);
            //VisUI.load(skin);
        }

        levelInfo = json.fromJson(Array.class, LevelInfo.class, Gdx.files.internal("levels.json"));

        assets.load("menuNavigate.wav", Sound.class);
        assets.load("titleMusic.wav", Music.class);
        // Todo: create loading screen
        assets.finishLoading();
        navigateSound = assets.get("menuNavigate.wav");
        titleMusic = assets.get("titleMusic.wav");
        titleMusic.setVolume(RobotRecharge.prefs.getMusicVolume());
        titleMusic.setLooping(true);
        titleMusic.play();
    }

    @Override
    public void dispose() {
        // Fonts are managed by skin
        skin.dispose();
        assets.dispose();
    }
}
