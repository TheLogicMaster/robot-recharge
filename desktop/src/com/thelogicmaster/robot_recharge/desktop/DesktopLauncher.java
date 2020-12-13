package com.thelogicmaster.robot_recharge.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.thelogicmaster.robot_recharge.ICodeEngine;
import com.thelogicmaster.robot_recharge.Language;
import com.thelogicmaster.robot_recharge.RobotRecharge;

import java.util.HashMap;

public class DesktopLauncher {
    public static void main(String[] arg) throws InterruptedException {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.allowSoftwareMode = true;
        config.addIcon("icon128.png", Files.FileType.Internal);
        config.addIcon("icon32.png", Files.FileType.Internal);
        config.addIcon("icon16.png", Files.FileType.Internal);
        HashMap<Language, ICodeEngine> engines = new HashMap<>();
        engines.put(Language.JavaScript, new DesktopJavaScriptEngine());
        new LwjglApplication(new RobotRecharge(engines, null), config);
    }
}
