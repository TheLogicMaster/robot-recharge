package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Gdx;

public class LevelObjective {

    private int value;
    private ObjectiveType type;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public ObjectiveType getType() {
        return type;
    }

    public void setType(ObjectiveType type) {
        this.type = type;
    }

    /**
     * Check whether objective is met
     * @param length Number of lines or blocks
     * @param time Level completion time
     * @return Objective met or not
     */
    public boolean check(int length, float time) {
        switch (type) {
            case LINES:
            case BLOCKS:
                return length <= value;
            case TIME:
                return time < value;
            default:
                Gdx.app.error("Unknown objective type", "" + type);
                return true;
        }
    }

    @Override
    public String toString() {
        switch (type) {
            case BLOCKS:
                return "Complete the level using no more than " + value + " code blocks";
            case TIME:
                return "Complete the level in under " + value + " seconds";
            case LINES:
                return "Complete the level using no more than " + value + " lines of code";
            default:
                Gdx.app.error("Unknown objective type", "" + type);
                return "Unknown Objective Type";
        }
    }
}
