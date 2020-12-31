package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.Language;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import java.util.HashMap;

public class IOSLauncher extends IOSApplication.Delegate {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        HashMap<Language, CodeEngine> engines = new HashMap<>();
        engines.put(Language.Lua, new LuaEngine());
        engines.put(Language.PHP, new PhpEngine());
        final Array<WindowMode> windowModes = new Array<>();
        return new IOSApplication(new RobotRecharge(engines, null, new PlatformUtils() {
            @Override
            public void setWindowMode(WindowMode windowMode) {
            }

            @Override
            public Array<WindowMode> getWindowModes() {
                return windowModes;
            }

            @Override
            public RobotController createRobotController(Robot robot, RobotExecutionListener listener, CodeEngine engine) {
                return new JavaRobotController(robot, listener, engine);
            }
        }, null, null, true) {
            @Override
            public void create() {
                RobotRecharge.gameServices = new IOSGameServices(((IOSApplication) Gdx.app).getUIViewController());
                super.create();
            }
        }, config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }
}