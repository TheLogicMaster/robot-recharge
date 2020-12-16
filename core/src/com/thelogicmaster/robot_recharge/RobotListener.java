package com.thelogicmaster.robot_recharge;

public interface RobotListener {

    void onRobotMove(Robot robot);

    void onRobotSubMove(Robot robot);

    void onRobotCrash(Robot robot, Position crash);
}
