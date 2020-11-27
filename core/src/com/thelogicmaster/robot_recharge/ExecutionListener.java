package com.thelogicmaster.robot_recharge;

public interface ExecutionListener {

    void onExecutionFinish();

    void onExecutionInterrupted();

    void onExecutionError(Exception e);
}
