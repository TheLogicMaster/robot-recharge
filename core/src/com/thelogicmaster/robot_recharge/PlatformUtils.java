package com.thelogicmaster.robot_recharge;

import com.thelogicmaster.robot_recharge.code.CodeEngine;

public interface PlatformUtils {

    void setWindowMode(WindowMode windowMode);

    RobotController createRobot(Robot controller, RobotExecutionListener listener, CodeEngine engine);
}
