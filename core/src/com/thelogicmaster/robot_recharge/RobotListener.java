package com.thelogicmaster.robot_recharge;

public interface RobotListener {

    void onRobotMove(Robot robot);

    // Todo: Ensure actually needed
    void onRobotSubMove(Robot robot);

    void onRobotCrash(Robot robot, Position crash);
}
