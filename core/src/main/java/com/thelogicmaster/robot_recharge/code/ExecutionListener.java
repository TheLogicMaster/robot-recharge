package com.thelogicmaster.robot_recharge.code;

public interface ExecutionListener {

    void onExecutionFinish();

    void onExecutionInterrupted();

    void onExecutionError(String error);
}
