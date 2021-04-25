package com.thelogicmaster.robot_recharge.code;

import com.thelogicmaster.robot_recharge.IRobot;

/**
 * A generic interface to a code running engine
 */
public interface CodeEngine {

    IExecutionInstance run(IRobot robot, String code, ExecutionListener listener);

    void initialize();
}
