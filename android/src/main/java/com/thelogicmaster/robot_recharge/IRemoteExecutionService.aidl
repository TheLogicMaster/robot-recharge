package com.thelogicmaster.robot_recharge;

import com.thelogicmaster.robot_recharge.IRemoteRobot;

interface IRemoteExecutionService {
    void run(String code, IRemoteRobot robot);
}