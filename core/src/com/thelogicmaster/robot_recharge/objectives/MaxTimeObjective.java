package com.thelogicmaster.robot_recharge.objectives;

import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.LevelEvent;

public class MaxTimeObjective implements Objective {

    private int time;

    public MaxTimeObjective() {
    }

    public MaxTimeObjective(int time) {
        this.time = time;
    }

    @Override
    public boolean check(int length, float time, Array<LevelEvent> events) {
        return time < this.time;
    }

    @Override
    public String getDescription(boolean useBlocks) {
        return "Complete the level in under " + time + " seconds";
    }
}
