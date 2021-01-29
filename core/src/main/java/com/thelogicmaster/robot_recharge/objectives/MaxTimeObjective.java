package com.thelogicmaster.robot_recharge.objectives;

import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.LevelEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaxTimeObjective implements Objective {

    private int time;

    @Override
    public boolean check(int length, int calls, float time, Array<LevelEvent> events) {
        return time < this.time;
    }

    @Override
    public String getDescription(boolean useBlocks) {
        return "Complete the level in under " + time + " seconds";
    }
}
