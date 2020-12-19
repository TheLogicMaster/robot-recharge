package com.thelogicmaster.robot_recharge.blocks;

import com.thelogicmaster.robot_recharge.structures.Elevator;

public class ElevatorBlock extends Block {
    private final Elevator elevator;

    public ElevatorBlock(Elevator elevator) {
        this.elevator = elevator;
    }

    @Override
    public boolean isSolid() {
        return super.isSolid();
    }
}
