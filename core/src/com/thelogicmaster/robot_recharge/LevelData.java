package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.code.Solution;
import com.thelogicmaster.robot_recharge.objectives.Objective;
import com.thelogicmaster.robot_recharge.structures.Structure;
import lombok.Data;

@Data
public class LevelData {
    private int xSize, ySize, zSize;
    private float levelHeight;
    private Direction startDirection;
    private Position startPosition;
    private Array<Structure> structures;
    private Array<Objective> objectives;
    private Array<Solution> solutions;
    private String levelModel;
    private String background;
}