package com.thelogicmaster.robot_recharge;

interface IRemoteRobot {

    void move(int distance);

    void turn(int distance);

    void sleep(double duration);

    void speak(String message);

    void interact();
}