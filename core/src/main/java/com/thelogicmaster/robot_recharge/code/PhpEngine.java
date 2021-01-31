package com.thelogicmaster.robot_recharge.code;

import com.badlogic.gdx.Gdx;
import com.caucho.quercus.QuercusEngine;
import com.caucho.quercus.QuercusException;
import com.thelogicmaster.robot_recharge.IRobot;

import java.io.IOException;

public class PhpEngine implements CodeEngine {

    private static IRobot robot;

    public static IRobot getRobot() {
        return robot;
    }

    @Override
    public Thread run(final IRobot robot, final String code, final ExecutionListener listener) {
        PhpEngine.robot = robot;
        Thread thread = new Thread(() -> {
            try {
                QuercusEngine engine = new QuercusEngine();
                engine.init();
                engine.execute("<?php\nimport com.thelogicmaster.robot_recharge.code.PhpEngine;\n$Robot=PhpEngine::getRobot();\n" + code + "\n?>");
                listener.onExecutionFinish();
            } catch (QuercusException e) {
                if (e.getCause() instanceof InterruptedException) {
                    listener.onExecutionInterrupted();
                    return;
                }
                Gdx.app.error("PHP", "Quercus", e);
                listener.onExecutionError(e.getMessage());
            } catch (IOException e) {
                Gdx.app.error("PHP", "IO", e);
                listener.onExecutionError(e.getMessage());
            }
        });
        thread.start();
        return thread;
    }
}
