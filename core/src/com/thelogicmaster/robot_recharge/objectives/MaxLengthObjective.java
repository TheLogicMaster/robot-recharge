package com.thelogicmaster.robot_recharge.objectives;

import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.LevelEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaxLengthObjective implements Objective {

    private int length;

    @Override
    public boolean check(int length, int calls, float time, Array<LevelEvent> events) {
        return length <= this.length;
    }

    @Override
    public String getDescription(boolean blocks) {
        return "Complete the level using no more than " + length + (blocks ? " Robot blocks" : " Robot references");
    }
}
