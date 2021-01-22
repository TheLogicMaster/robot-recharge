package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.objectives.Objective;

public interface LevelExecutionListener {

    void onLevelIncomplete(Array<Objective> failed);

    void onLevelFail(String reason);

    void onLevelComplete(float completionTime, int length, int calls);

    void onLevelError(Exception e);

    void onLevelAbort();

    void onLevelPause();
}
