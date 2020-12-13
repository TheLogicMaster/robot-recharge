package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.thelogicmaster.robot_recharge.code.ICodeEngine;
import com.thelogicmaster.robot_recharge.code.Language;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import java.util.HashMap;

public class IOSLauncher extends IOSApplication.Delegate {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        return new IOSApplication(new RobotRecharge(new HashMap<Language, ICodeEngine>(), null, new IPlatformUtils() {
            @Override
            public void setWindowMode(WindowMode windowMode) {
            }
        }), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }
}