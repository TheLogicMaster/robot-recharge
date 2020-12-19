package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Gdx;
import com.squareup.duktape.Duktape;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.ExecutionListener;

public class AndroidJavaScriptEngine implements CodeEngine {

    @Override
    public Thread run(final IRobot robot, final String code, final ExecutionListener listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (Duktape duktape = Duktape.create()) {
                    duktape.set("Robot", IRobot.class, robot);
                    duktape.evaluate(code);
                    listener.onExecutionFinish();
                } catch (Exception e) {
                    if (e instanceof InterruptedException) {
                        listener.onExecutionInterrupted();
                        return;
                    }
                    Gdx.app.error("Duktape", e.toString());
                    listener.onExecutionError(e);
                }
            }
        });
        thread.start();
        return thread;
    }
}
