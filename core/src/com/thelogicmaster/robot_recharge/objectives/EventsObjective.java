package com.thelogicmaster.robot_recharge.objectives;

import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.LevelEvent;

/**
 * An objective that requires all specified events to be called at least once
 */
public class EventsObjective implements Objective {

    private Array<String> events = new Array<>();
    private String description;

    public EventsObjective() {
    }

    public EventsObjective(Array<String> events, String description) {
        this.events = events;
        this.description = description;
    }

    @Override
    public boolean check(int length, float time, Array<LevelEvent> events) {
        // Todo: switch to Array.containsAll and create equals method for LevelEvent that compares a string, if possible
        for (String name: new Array.ArrayIterator<>(this.events)) {
            boolean found = false;
            for (LevelEvent event : new Array.ArrayIterator<>(events))
                if (event.toString().equals(name)) {
                    found = true;
                    break;
                }
            if (!found)
                return false;
        }
        return true;
    }

    @Override
    public String getDescription(boolean useBlocks) {
        return description;
    }
}
