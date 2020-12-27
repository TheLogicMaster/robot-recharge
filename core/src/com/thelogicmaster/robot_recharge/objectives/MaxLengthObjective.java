package com.thelogicmaster.robot_recharge.objectives;

import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.LevelEvent;

public class MaxLengthObjective implements Objective {

    private int length;

    public MaxLengthObjective() {
    }

    public MaxLengthObjective(int length) {
        this.length = length;
    }

    @Override
    public boolean check(int length, float time, Array<LevelEvent> events) {
        return length <= this.length;
    }

    @Override
    public String getDescription(boolean blocks) {
        return "Complete the level using no more than " + length + (blocks ? " Robot blocks" : " Robot function calls");
    }
}
