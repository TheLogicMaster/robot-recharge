package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.caucho.quercus.QuercusEngine;
import com.kotcrab.vis.ui.VisUI;
import com.thelogicmaster.robot_recharge.screens.GameScreen;
import com.thelogicmaster.robot_recharge.screens.TitleScreen;

import java.util.HashMap;
import java.util.Map;

public class RobotRecharge extends Game {

    public static IBlocklyEditor blocksEditor;
    public static RobotRecharge instance;
    public static RobotAssets assets;
    public static Map<Language, ICodeEngine> codeEngines = new HashMap<>();

    private TitleScreen titleScreen;

    public RobotRecharge(Map<Language, ICodeEngine> engines, IBlocklyEditor blocksEditor) {
        RobotRecharge.blocksEditor = blocksEditor;
        instance = this;
        codeEngines = engines;
    }

    @Override
    public void create() {
        assets = new RobotAssets();

        codeEngines.put(Language.Lua, new LuaEngine());
        codeEngines.put(Language.PHP, new PhpEngine());

        Gdx.files.local("save/").mkdirs();

        LevelSave levelData = new LevelSave("test", false,
                "while true do\n  Robot:sleep(1);\n  Robot:move(1);\n  Robot:turn(1);\n  end", Language.Lua);
        levelData = new LevelSave("test", false,
                "while True:\n  Robot.sleep(1)\n  Robot.move(1)\n  Robot.turn(1)\n", Language.Python);
        FileHandle save = Gdx.files.internal("save/test.txt");
        if (save.exists())
            levelData = Helpers.json.fromJson(LevelSave.class, save);
        setScreen(new GameScreen(levelData));
        titleScreen = new TitleScreen();
        //setScreen(titleScreen);
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
