package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.thelogicmaster.robot_recharge.code.*;
import com.thelogicmaster.robot_recharge.screens.GameScreen;
import com.thelogicmaster.robot_recharge.screens.TitleScreen;

import java.util.HashMap;
import java.util.Map;

public class RobotRecharge extends Game {

    public static BlocklyEditor blocksEditor;
    public static RobotRecharge instance;
    public static RobotAssets assets;
    public static Map<Language, CodeEngine> codeEngines = new HashMap<>();
    public static PlatformUtils platformUtils;
    public static TTSEngine ttsEngine;

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

        if (Gdx.app.getType() != Application.ApplicationType.WebGL)
            Gdx.files.local("save/").mkdirs();

        /*LevelSave levelData = new LevelSave("test", false,
                "while true do\n  Robot:sleep(1);\n  Robot:move(2);\n  Robot:turn(1);\n  end", Language.Lua);
        FileHandle save = Gdx.files.internal("save/test.txt");
        if (save.exists())
            levelData = RobotUtils.json.fromJson(LevelSave.class, save);
        setScreen(new GameScreen(levelData));*/
        titleScreen = new TitleScreen();
        setScreen(titleScreen);
    }

    public void returnToTitle() {
        setScreen(titleScreen);
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
