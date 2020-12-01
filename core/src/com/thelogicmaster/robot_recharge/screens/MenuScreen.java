package com.thelogicmaster.robot_recharge.screens;

import com.thelogicmaster.robot_recharge.RobotRecharge;

public abstract class MenuScreen extends RobotScreen {

    private RobotScreen previousScreen;

    public MenuScreen(RobotScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    protected void showPreviousScreen() {
        RobotRecharge.instance.setScreen(previousScreen);
    }
}
