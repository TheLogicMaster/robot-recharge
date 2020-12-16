package com.thelogicmaster.robot_recharge;

import com.thelogicmaster.robot_recharge.code.ExecutionListener;

public interface RobotExecutionListener extends ExecutionListener {

    void onExecutionPaused();
}
