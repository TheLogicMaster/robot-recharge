package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.thelogicmaster.robot_recharge.code.BlocklyEditor;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.Language;
import com.thelogicmaster.robot_recharge.screens.TitleScreen;
import de.golfgl.gdxgamesvcs.IGameServiceClient;
import de.golfgl.gdxgamesvcs.IGameServiceListener;

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
    public static IGameServiceClient gameServices;
    public static boolean debug;

    private TitleScreen titleScreen;

    public RobotRecharge(Map<Language, CodeEngine> codeEngines, BlocklyEditor blocksEditor, PlatformUtils platformUtils, TTSEngine ttsEngine, IGameServiceClient gameServices, boolean debug) {
        // Todo: switch to config object instead of countless constructor args
        RobotRecharge.blocksEditor = blocksEditor;
        RobotRecharge.codeEngines = codeEngines;
        RobotRecharge.platformUtils = platformUtils;
        RobotRecharge.ttsEngine = ttsEngine;
        RobotRecharge.gameServices = gameServices;
        RobotRecharge.debug = debug;
        instance = this;
    }

    @Override
    public void create() {
        if (debug)
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
        prefs = new PreferencesHelper();
        assets = new RobotAssets();
        titleScreen = new TitleScreen();

        gameServices.setListener(new IGameServiceListener() {
            @Override
            public void gsOnSessionActive() {

            }

            @Override
            public void gsOnSessionInactive() {

            }

            @Override
            public void gsShowErrorToUser(GsErrorType et, String msg, Throwable t) {

            }
        });
        gameServices.resumeSession();

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
    public void pause() {
        gameServices.pauseSession();
        super.pause();
    }

    @Override
    public void resume() {
        gameServices.resumeSession();
        super.resume();
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
