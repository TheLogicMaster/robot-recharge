package com.thelogicmaster.robot_recharge;

import com.thelogicmaster.robot_recharge.blocks.Block;

public class BlockPlaceholder {

    private String block;
    private Position position;

    public BlockPlaceholder() {
    }

    public BlockPlaceholder(String block, Position position) {
        this.block = block;
        this.position = position;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "BlockPlaceholder{" +
                "block='" + block + '\'' +
                ", position=" + position +
                '}';
    }
}
