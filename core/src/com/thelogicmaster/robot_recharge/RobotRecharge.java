package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.thelogicmaster.robot_recharge.code.BlocklyEditor;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.Language;
import com.thelogicmaster.robot_recharge.screens.TitleScreen;

import java.util.HashMap;
import java.util.Map;

public class RobotRecharge extends Game {

    public static BlocklyEditor blocksEditor;
    public static RobotRecharge instance;
    @SuppressWarnings("LibGDXStaticResource")
    public static RobotAssets assets;
    public static Map<Language, CodeEngine> codeEngines = new HashMap<>();
    public static PlatformUtils platformUtils;
    public static TTSEngine ttsEngine;
    public static PreferencesHelper prefs;

    private TitleScreen titleScreen;

    public RobotRecharge(Map<Language, CodeEngine> codeEngines, BlocklyEditor blocksEditor, PlatformUtils platformUtils, TTSEngine ttsEngine) {
        RobotRecharge.blocksEditor = blocksEditor;
        RobotRecharge.codeEngines = codeEngines;
        RobotRecharge.platformUtils = platformUtils;
        RobotRecharge.ttsEngine = ttsEngine;
        instance = this;
    }

    @Override
    public void create() {
        assets = new RobotAssets();
        prefs = new PreferencesHelper();
        titleScreen = new TitleScreen();
        setScreen(titleScreen);
    }

    public void returnToTitle() {
        setScreen(titleScreen);
        assets.titleMusic.play();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (RobotRecharge.blocksEditor != null)
            RobotRecharge.blocksEditor.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void dispose() {
        super.dispose();
        assets.dispose();
        titleScreen.dispose();
    }
}
