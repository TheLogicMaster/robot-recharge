package com.thelogicmaster.robot_recharge.desktop;

import com.badlogic.gdx.Gdx;
import com.thelogicmaster.robot_recharge.code.ExecutionListener;
import com.thelogicmaster.robot_recharge.code.ICodeEngine;
import com.thelogicmaster.robot_recharge.IRobot;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;

public class DesktopPythonEngine implements ICodeEngine {

    @Override
    public Thread run(final IRobot robot, final String code, final ExecutionListener listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try(PythonInterpreter python = new PythonInterpreter()) {
                    python.set("Robot", robot);
                    python.exec(code);
                    listener.onExecutionFinish();
                } catch (PyException e) {
                    if (e.getCause() instanceof InterruptedException) {
                        listener.onExecutionInterrupted();
                        return;
                    }
                    Gdx.app.error("Python", "PyException", e);
                    listener.onExecutionError(e);
                }
            }
        });
        thread.start();
        return thread;
    }
}
