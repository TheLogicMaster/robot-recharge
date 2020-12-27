package com.thelogicmaster.robot_recharge.objectives;

import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.LevelEvent;

public interface Objective {

    boolean check(int length, float time, Array<LevelEvent> events);

    String getDescription(boolean useBlocks);
}
