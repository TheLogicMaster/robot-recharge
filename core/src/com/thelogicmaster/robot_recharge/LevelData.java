package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.structures.Structure;

public class LevelData {
    private int xSize, ySize, zSize;
    private float levelHeight;
    private Array<Structure> structures = new Array<>();
    private Array<LevelObjective> objectives = new Array<>();
    private String levelModelName;

    public int getXSize() {
        return xSize;
    }

    public void setXSize(int xSize) {
        this.xSize = xSize;
    }

    public int getYSize() {
        return ySize;
    }

    public void setYSize(int ySize) {
        this.ySize = ySize;
    }

    public int getZSize() {
        return zSize;
    }

    public void setZSize(int zSize) {
        this.zSize = zSize;
    }

    public float getLevelHeight() {
        return levelHeight;
    }

    public void setLevelHeight(float levelHeight) {
        this.levelHeight = levelHeight;
    }

    public String getLevelModelName() {
        return levelModelName;
    }

    public void setLevelModelName(String levelModelName) {
        this.levelModelName = levelModelName;
    }

    public Array<Structure> getStructures() {
        return structures;
    }

    public void setStructures(Array<Structure> structures) {
        this.structures = structures;
    }

    public Array<LevelObjective> getObjectives() {
        return objectives;
    }

    public void setObjectives(Array<LevelObjective> objectives) {
        this.objectives = objectives;
    }
}
