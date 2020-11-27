package com.thelogicmaster.robot_recharge.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.thelogicmaster.robot_recharge.RobotGame;

public class DesktopLauncher {
	public static void main (String[] arg) throws InterruptedException {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.addIcon("icon128.png", Files.FileType.Internal);
		config.addIcon("icon32.png", Files.FileType.Internal);
		config.addIcon("icon16.png", Files.FileType.Internal);
		new LwjglApplication(new RobotGame(new DesktopJavaScriptEngine(), null), config);
	}
}
