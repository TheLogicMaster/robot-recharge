package com.thelogicmaster.robot_recharge.objectives;

import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.LevelEvent;

/**
 * An objective that requires a certain number of occurrences of a specified event
 * Can require exactly that many occurrences or greater than or equal
 */
public class EventCountObjective implements Objective {

    private String event;
    private int count;
    private boolean exact;
    private String description;

    public EventCountObjective() {
    }

    public EventCountObjective(String event, int count, boolean exact, String description) {
        this.event = event;
        this.count = count;
        this.exact = exact;
        this.description = description;
    }

    @Override
    public boolean check(int length, int calls, float time, Array<LevelEvent> events) {
        int number = 0;
        for (LevelEvent event : new Array.ArrayIterator<>(events))
            if (event.toString().equals(this.event))
                number++;
        if (exact)
            return number == count;
        else
            return number >= count;
    }

    @Override
    public String getDescription(boolean useBlocks) {
        return description;
    }
}
