package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
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
        return new IOSApplication(new RobotRecharge(engines, null, new PlatformUtils() {
            @Override
            public void setWindowMode(WindowMode windowMode) {
            }

            @Override
            public RobotController createRobot(Robot controller, RobotExecutionListener listener, CodeEngine engine) {
                return new JavaRobotController(controller, listener, engine);
            }
        }, null), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }
}