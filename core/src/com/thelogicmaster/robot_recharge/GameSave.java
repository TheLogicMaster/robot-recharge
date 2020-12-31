package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.utils.Array;

/**
 * Represents an entire cloud save
 */
public class GameSave {

    private Array<LevelSave> levelSaves;
    private int unlockedLevel;

    public GameSave() {
    }

    public GameSave(Array<LevelSave> levelSaves, int unlockedLevel) {
        this.levelSaves = levelSaves;
        this.unlockedLevel = unlockedLevel;
    }

    public Array<LevelSave> getLevelSaves() {
        return levelSaves;
    }

    public void setLevelSaves(Array<LevelSave> levelSaves) {
        this.levelSaves = levelSaves;
    }

    public int getUnlockedLevel() {
        return unlockedLevel;
    }

    public void setUnlockedLevel(int unlockedLevel) {
        this.unlockedLevel = unlockedLevel;
    }
}
