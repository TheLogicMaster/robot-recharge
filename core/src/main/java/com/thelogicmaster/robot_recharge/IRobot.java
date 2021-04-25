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
}
