package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.code.CodeEngine;

public interface PlatformUtils {

    void setWindowMode(WindowMode windowMode);

    RobotController createRobotController(Robot robot, RobotExecutionListener listener, CodeEngine engine);

    Array<WindowMode> getWindowModes();
}
