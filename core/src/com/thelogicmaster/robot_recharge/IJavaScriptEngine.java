package com.thelogicmaster.robot_recharge;

public interface IJavaScriptEngine {

    Thread run(IRobot robot, String code, ExecutionListener listener);
}
