package com.thelogicmaster.robot_recharge.blocks;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.thelogicmaster.robot_recharge.structures.MovingPlatform;

public class MovingPlatformBlock extends Block {
    private final MovingPlatform platform;

    public MovingPlatformBlock(MovingPlatform platform, ModelInstance modelInstance) {
        this.platform = platform;
        this.model = modelInstance;
    }

    @Override
    public boolean isSolid() {
        return platform.getPlatformPosition().equals(getPosition());
    }
}
