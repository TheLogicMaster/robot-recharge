package com.thelogicmaster.robot_recharge.code.java;

import com.badlogic.gdx.Gdx;
import com.caucho.quercus.QuercusEngine;
import com.caucho.quercus.QuercusException;
import com.thelogicmaster.robot_recharge.IRobot;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.ExecutionListener;

import java.io.IOException;

public class PhpEngine implements CodeEngine {

    private static IRobot robot;

    public static IRobot getRobot() {
        return robot;
    }

    @Override
    public void initialize () {
    }

    @Override
    public ExecutionInstance run(final IRobot robot, final String code, final ExecutionListener listener) {
        PhpEngine.robot = robot;
        Thread thread = new Thread(() -> {
            QuercusEngine engine = new QuercusEngine();
            try {
                engine.init();
                engine.execute("<?php\n"
                    + "import com.thelogicmaster.robot_recharge.code.java.PhpEngine;\n"
                    + "$Robot=PhpEngine::getRobot();\n"
                    + code + "\n"
                    + "?>");
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
            } finally {
                engine.getQuercus().close();
            }
        });
        thread.start();
        return new ExecutionInstance(thread);
    }
}
