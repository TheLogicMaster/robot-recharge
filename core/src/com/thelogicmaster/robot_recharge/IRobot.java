package com.thelogicmaster.robot_recharge;

public interface IRobot {

    void move(int distance) throws InterruptedException;

    void turn(int distance) throws InterruptedException;

    void sleep(double duration) throws InterruptedException;
}
