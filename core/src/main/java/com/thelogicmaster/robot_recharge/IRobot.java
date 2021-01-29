package com.thelogicmaster.robot_recharge;

/**
 * An interface to the RobotController from the Robot and Engine
 */
public interface IRobot {

    void move(int distance) throws InterruptedException;

    void turn(int distance) throws InterruptedException;

    void sleep(double duration) throws InterruptedException;

    void speak(String message);

    void interact();

    void start();

    void stop();

    void pause();

    void setFastForward(boolean fastForward);

    void setCode(String code);

    boolean isRunning();
}
