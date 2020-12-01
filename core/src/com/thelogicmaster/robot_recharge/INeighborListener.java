package com.thelogicmaster.robot_recharge;

import com.thelogicmaster.robot_recharge.blocks.Block;

/**
 * Used on a block that needs to listen to neighbor block changes
 */
public interface INeighborListener {
    void onNeighborChange(Position position, Block block, String event);
}
