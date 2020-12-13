package com.thelogicmaster.robot_recharge.code;

import com.thelogicmaster.robot_recharge.IRobot;

/**
 * A generic interface to a JavaScript running engine
 */
public interface ICodeEngine {

    Thread run(IRobot robot, String code, ExecutionListener listener);
}
