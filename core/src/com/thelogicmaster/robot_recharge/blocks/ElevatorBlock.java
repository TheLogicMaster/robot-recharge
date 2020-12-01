package com.thelogicmaster.robot_recharge.blocks;

import com.thelogicmaster.robot_recharge.Position;
import com.thelogicmaster.robot_recharge.structures.Elevator;

public class ElevatorBlock extends Block {
    private Elevator elevator;

    public ElevatorBlock(Elevator elevator) {
        this.elevator = elevator;
    }
}
