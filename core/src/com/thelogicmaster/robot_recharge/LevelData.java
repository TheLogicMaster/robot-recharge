package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.code.Solution;
import com.thelogicmaster.robot_recharge.objectives.Objective;
import com.thelogicmaster.robot_recharge.structures.Structure;

public class LevelData {
    private int xSize, ySize, zSize;
    private float levelHeight;
    private Array<Structure> structures;
    private Array<Objective> objectives;
    private Array<Solution> solutions;
    private String levelModel;
    private String background;

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

    public String getLevelModel() {
        return levelModel;
    }

    public void setLevelModel(String levelModel) {
        this.levelModel = levelModel;
    }

    public Array<Structure> getStructures() {
        return structures;
    }

    public void setStructures(Array<Structure> structures) {
        this.structures = structures;
    }

    public Array<Objective> getObjectives() {
        return objectives;
    }

    public void setObjectives(Array<Objective> objectives) {
        this.objectives = objectives;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public Array<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(Array<Solution> solutions) {
        this.solutions = solutions;
    }
}
