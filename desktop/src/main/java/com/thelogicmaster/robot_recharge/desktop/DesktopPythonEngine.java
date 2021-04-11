package com.thelogicmaster.robot_recharge.desktop;

import com.badlogic.gdx.Gdx;
import com.thelogicmaster.robot_recharge.IRobot;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.ExecutionInstance;
import com.thelogicmaster.robot_recharge.code.ExecutionListener;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;

public class DesktopPythonEngine implements CodeEngine {

    @Override
    public ExecutionInstance run(final IRobot robot, final String code, final ExecutionListener listener) {
        PythonInterpreter python = new PythonInterpreter();
        Thread thread = new Thread(() -> {
            try {
                python.close();
                python.set("Robot", robot);
                python.exec(code);
                listener.onExecutionFinish();
                python.close();
            } catch (PyException e) {
                python.close();
                if (e.getCause() instanceof InterruptedException) {
                    listener.onExecutionInterrupted();
                    return;
                }
                Gdx.app.error("Python", "PyException", e);
                listener.onExecutionError(e.getMessage());
            }
        });
        thread.start();
        return new ExecutionInstance(thread) {
            @Override
            protected void stop () {
                super.stop();

                python.close();
            }
        };
    }
}
