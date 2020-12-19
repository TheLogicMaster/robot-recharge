package com.thelogicmaster.robot_recharge.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.thelogicmaster.robot_recharge.PlatformUtils;
import com.thelogicmaster.robot_recharge.RobotRecharge;
import com.thelogicmaster.robot_recharge.WindowMode;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.Language;

import java.util.HashMap;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.allowSoftwareMode = true;
        config.addIcon("icon128.png", Files.FileType.Internal);
        config.addIcon("icon32.png", Files.FileType.Internal);
        config.addIcon("icon16.png", Files.FileType.Internal);
        HashMap<Language, CodeEngine> engines = new HashMap<>();
        engines.put(Language.JavaScript, new DesktopJavaScriptEngine());
        engines.put(Language.Python, new DesktopPythonEngine());
        new LwjglApplication(new RobotRecharge(engines, null, new PlatformUtils() {
            @Override
            public void setWindowMode(WindowMode windowMode) {
                switch (windowMode) {
                    case Windowed:
                        Gdx.graphics.setUndecorated(false);
                        Gdx.graphics.setWindowedMode(960, 540);
                        break;
                    case Fullscreen:
                        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                        break;
                    case WindowedFullscreen:
                        Gdx.graphics.setUndecorated(true);
                        Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
                        Gdx.graphics.setWindowedMode(mode.width, mode.height);
                        break;
                }
            }
        }, new DesktopTTSEngine()), config) {
            @Override
            protected void mainLoop() {
                try {
                    super.mainLoop();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        };
    }
}
