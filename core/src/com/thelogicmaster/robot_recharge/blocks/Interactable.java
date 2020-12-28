package com.thelogicmaster.robot_recharge.blocks;

import com.thelogicmaster.robot_recharge.Robot;

/**
 * An interface for blocks that can be interacted with by the robot
 */
public interface Interactable {

    /**
     * Interact with from robot
     *
     * @param robot Robot that's interacting
     */
    void interact(Robot robot);
}
