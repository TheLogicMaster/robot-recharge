package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.objectives.Objective;

public interface LevelExecutionListener {

    void onLevelFail(Array<Objective> failed);

    void onLevelComplete(float completionTime);

    void onLevelError(Exception e);

    void onLevelAbort();

    void onLevelPause();
}
