package com.thelogicmaster.robot_recharge;

/**
 * A generic interface to a JavaScript running engine
 */
public interface IJavaScriptEngine {

    Thread run(IRobot robot, String code, ExecutionListener listener);
}
