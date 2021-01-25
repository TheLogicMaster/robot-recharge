package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.thelogicmaster.robot_recharge.code.BlocklyEditor;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.Language;
import com.thelogicmaster.robot_recharge.screens.RobotScreen;
import com.thelogicmaster.robot_recharge.screens.TitleScreen;
import de.golfgl.gdxgamesvcs.IGameServiceListener;

import java.util.Map;

public class RobotRecharge extends Game implements IGameServiceListener {

    public static RobotRecharge instance;
    public static BlocklyEditor blocksEditor;
    public static RobotAssets assets;
    public static Map<Language, CodeEngine> codeEngines;
    public static PlatformUtils platformUtils;
    public static TTSEngine ttsEngine;
    public static RobotPreferences prefs;
    public static GameServices gameServices;
    public static CodeEditorUtils codeEditorUtils;
    public static boolean debug;

    private TitleScreen titleScreen;

    public RobotRecharge(Map<Language, CodeEngine> codeEngines, BlocklyEditor blocksEditor, PlatformUtils platformUtils, TTSEngine ttsEngine, GameServices gameServices, CodeEditorUtils codeEditorUtils, boolean debug) {
        // Todo: switch to config object instead of countless constructor args, or directly setting fields from platforms
        // Todo: Combine editorUtils with RobotController creation for a Java and JavaScript specific implementations
        RobotRecharge.blocksEditor = blocksEditor;
        RobotRecharge.codeEngines = codeEngines;
        RobotRecharge.platformUtils = platformUtils;
        RobotRecharge.ttsEngine = ttsEngine;
        RobotRecharge.gameServices = gameServices;
        RobotRecharge.codeEditorUtils = codeEditorUtils;
        RobotRecharge.debug = debug;
        instance = this;
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(debug ? Application.LOG_DEBUG : Application.LOG_ERROR);
        prefs = new RobotPreferences();
        assets = new RobotAssets();
        titleScreen = new TitleScreen();

        gameServices.setListener(this);

        if (RobotUtils.usesGameJolt() && gameServices.needsCredentials() && prefs.hasGameJoltCredentials())
            gameServices.setCredentials(prefs.getGameJoltUsername(), prefs.getGameJoltToken());

        if (Gdx.app.getType() == Application.ApplicationType.Android || (RobotUtils.usesGameJolt() && !gameServices.needsCredentials()))
            gameServices.resumeSession();

        if (Gdx.app.getType() != Application.ApplicationType.WebGL)
            RobotRecharge.platformUtils.setWindowMode(RobotRecharge.prefs.getWindowMode());

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

    @Override
    public void gsOnSessionActive() {
        if (prefs.hasRestoredSave())
            return;
        gameServices.fetchGameStates(gameStates -> {
            prefs.setRestoredSave();
            if (gameStates.size == 0)
                return;
            boolean purchases = Gdx.app.getType() == Application.ApplicationType.Android;
            Dialogs.showOptionDialog(((RobotScreen) getScreen()).getStage(),
                    "Restore " + (purchases ? "Purchases and " : "") + "Save Data?",
                    "It looks like you have played before, \nattempt to restore " +
                            (purchases ? "purchases and" : "") + " save data?",
                    Dialogs.OptionDialogType.YES_CANCEL, new OptionDialogAdapter() {
                        @Override
                        public void yes() {
                            RobotUtils.playNavigationSound();
                            RobotUtils.restorePurchases();
                            RobotUtils.loadCloudSave();
                            returnToTitle();
                        }

                        @Override
                        public void cancel() {
                            RobotUtils.playNavigationSound();
                        }
                    });
        });
    }

    @Override
    public void gsOnSessionInactive() {
    }

    @Override
    public void gsShowErrorToUser(GsErrorType et, String msg, Throwable t) {
        Gdx.app.error("GameServices", "Error: " + et + " :" + msg, t);
        Dialogs.showErrorDialog(((RobotScreen) getScreen()).getStage(), msg, t);
    }
}
