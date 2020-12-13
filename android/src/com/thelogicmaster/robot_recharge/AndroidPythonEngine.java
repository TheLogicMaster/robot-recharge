package com.thelogicmaster.robot_recharge;

import android.content.Context;
import com.badlogic.gdx.Gdx;
import com.chaquo.python.PyException;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.chaquo.python.android.PyApplication;

public class AndroidPythonEngine implements ICodeEngine {

    public static IRobot robot;

    public AndroidPythonEngine(Context context) {
        Python.start(new AndroidPlatform(context));
    }

    @Override
    public Thread run(final IRobot robot, final String code, final ExecutionListener listener) {
        AndroidPythonEngine.robot = robot;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
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
                    listener.onExecutionError(e);
                }
            }
        });
        thread.start();
        return thread;
    }
}
