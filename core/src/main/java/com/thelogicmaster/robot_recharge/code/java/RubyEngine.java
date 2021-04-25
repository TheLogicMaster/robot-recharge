package com.thelogicmaster.robot_recharge.code.java;

import com.badlogic.gdx.Gdx;
import com.thelogicmaster.robot_recharge.IRobot;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.ExecutionListener;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.javasupport.JavaUtil;

public class RubyEngine implements CodeEngine {

    @Override
    public void initialize () {
    }

    @Override
    public ExecutionInstance run(final IRobot robot, final String code, final ExecutionListener listener) {
        Thread thread = new Thread(() -> {
            RubyInstanceConfig config = new RubyInstanceConfig();
            config.setCompileMode(RubyInstanceConfig.CompileMode.OFF);
            Ruby ruby = Ruby.newInstance(config);
            ruby.defineGlobalConstant("Robot", JavaUtil.convertJavaToRuby(ruby, robot));
            try {
                ruby.evalScriptlet(code);
                JavaEmbedUtils.terminate(ruby);
                listener.onExecutionFinish();
            } catch (Exception e) {
                //noinspection ConstantConditions
                if (e instanceof InterruptedException) {
                    listener.onExecutionInterrupted();
                    return;
                }
                Gdx.app.error("Ruby", e.toString());
                listener.onExecutionError(e.getMessage());
            } finally {
                ruby.tearDown();
            }
        });
        thread.start();
        return new ExecutionInstance(thread);
    }
}
