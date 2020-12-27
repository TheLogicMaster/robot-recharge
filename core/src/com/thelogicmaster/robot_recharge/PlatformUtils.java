package com.thelogicmaster.robot_recharge;

import com.thelogicmaster.robot_recharge.code.CodeEngine;

public interface PlatformUtils {

    void setWindowMode(WindowMode windowMode);

    RobotController createRobotController(Robot robot, RobotExecutionListener listener, CodeEngine engine);
}
