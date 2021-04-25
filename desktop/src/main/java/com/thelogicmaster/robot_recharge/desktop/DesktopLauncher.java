package com.thelogicmaster.robot_recharge.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.*;
import com.thelogicmaster.robot_recharge.code.java.BasicEngine;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.java.JavaCodeEditorUtils;
import com.thelogicmaster.robot_recharge.code.java.JavaRobotController;
import com.thelogicmaster.robot_recharge.code.Language;
import com.thelogicmaster.robot_recharge.code.java.LuaEngine;
import com.thelogicmaster.robot_recharge.code.java.PhpEngine;
import com.thelogicmaster.robot_recharge.code.java.RubyEngine;

import java.util.HashMap;

public class DesktopLauncher {
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "robot-recharge";
		config.width = 1920;
		config.height = 1080;
		config.allowSoftwareMode = true;
		// This prevents a confusing error that would appear after exiting normally.
		config.forceExit = false;
		HashMap<Language, CodeEngine> engines = new HashMap<>();
		engines.put(Language.JavaScript, new DesktopJavaScriptEngine());
		engines.put(Language.Python, new DesktopPythonEngine());
		engines.put(Language.Lua, new LuaEngine());
		engines.put(Language.PHP, new PhpEngine());
		engines.put(Language.Basic, new BasicEngine());
		engines.put(Language.Ruby, new RubyEngine());
		final Array<WindowMode> windowModes = new Array<>(new WindowMode[]{
				WindowMode.Windowed,
				WindowMode.Fullscreen,
				WindowMode.WindowedFullscreen
		});

		for (int size : new int[] { 128, 64, 32, 16 })
			config.addIcon("icons/icon" + size + ".png", FileType.Internal);

		new LwjglApplication(new RobotRecharge(engines, null, new PlatformUtils() {
			@Override
			public void setWindowMode(WindowMode windowMode) {
				switch (windowMode) {
					case Windowed:
						Gdx.graphics.setUndecorated(false);
						Gdx.graphics.setWindowedMode(1920, 1080);
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

			@Override
			public Array<WindowMode> getWindowModes() {
				return windowModes;
			}

			@Override
			public RobotController createRobotController(Robot robot, RobotExecutionListener listener, CodeEngine engine) {
				return new JavaRobotController(robot, listener, engine);
			}
		}, new DesktopTTSEngine(), new DesktopGameServices(), new JavaCodeEditorUtils(), BuildConfig.DEBUG), config);
	}
}