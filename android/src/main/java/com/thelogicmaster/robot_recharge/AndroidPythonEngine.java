package com.thelogicmaster.robot_recharge;

import android.content.Context;
import com.badlogic.gdx.Gdx;
import com.chaquo.python.PyException;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.ExecutionListener;

public class AndroidPythonEngine implements CodeEngine {

    public static IRobot robot;

    public AndroidPythonEngine(Context context) {
        if (!Python.isStarted())
            Python.start(new AndroidPlatform(context));
    }

    @Override
    public ExecutionInstance run(final IRobot robot, final String code, final ExecutionListener listener) {
        AndroidPythonEngine.robot = robot;
        Thread thread = new Thread(() -> {
            try {
                Python py = Python.getInstance();
                PyObject engine = py.getModule("engine");
                engine.callAttr("run", code);
                listener.onExecutionFinish();
            } catch (PyException e) {
                if (e.getCause() instanceof InterruptedException) {
                    listener.onExecutionInterrupted();
                    return;
                }
                Gdx.app.error("Python", "PyException", e);
                listener.onExecutionError(e.getMessage());
            }
        });
        thread.start();
        return new ExecutionInstance(thread);
    }
}
