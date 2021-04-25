package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Gdx;
import com.squareup.duktape.Duktape;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.ExecutionListener;

public class AndroidJavaScriptEngine implements CodeEngine {

    @Override
    public void initialize () {
    }

    @Override
    public ExecutionInstance run(final IRobot robot, final String code, final ExecutionListener listener) {
        Duktape duktape = Duktape.create();
        Thread thread = new Thread(() -> {
            try {
                duktape.set("Robot", IRobot.class, robot);
                duktape.evaluate(code);
                listener.onExecutionFinish();
            } catch (Exception e) {
                //noinspection ConstantConditions
                if (e instanceof InterruptedException ||
                    (e.getMessage() != null && e.getMessage().contains("RangeError: execution timeout"))) {
                    listener.onExecutionInterrupted();
                    return;
                }
                Gdx.app.error("Duktape", e.toString());
                listener.onExecutionError(e.getMessage());
            } finally {
                duktape.close();
            }
        });
        thread.start();
        return new ExecutionInstance(thread) {
            @Override
            public void stop () {
                super.stop();
                duktape.interrupt();
            }
        };
    }
}
