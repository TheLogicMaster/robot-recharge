package com.thelogicmaster.robot_recharge;

public interface RobotController {

    void start();

    void pause();

    void stop();

    void setFastForward(boolean fastForward);

    void setCode(String code);

    boolean isRunning();
}
