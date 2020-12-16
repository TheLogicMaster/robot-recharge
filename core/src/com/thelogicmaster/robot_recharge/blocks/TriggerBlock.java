package com.thelogicmaster.robot_recharge.blocks;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.Level;
import com.thelogicmaster.robot_recharge.Robot;
import com.thelogicmaster.robot_recharge.RobotListener;
import com.thelogicmaster.robot_recharge.RobotListenerAdaptor;

public class TriggerBlock extends Block {

    private boolean destroy;
    private boolean oneShot;

    private transient boolean triggered;

    @Override
    public void assetsLoaded(AssetManager assetManager) {
        super.assetsLoaded(assetManager);
        if (modelSource != null)
            for (Material mat : new Array.ArrayIterator<>(modelSource.materials))
                mat.set(new BlendingAttribute(0.7f));
    }

    @Override
    public void setup(final Level level) {
        super.setup(level);
        level.addRobotListener(new RobotListenerAdaptor() {
            @Override
            public void onRobotMove(Robot robot) {
                if (robot.getBlockPos().equals(getPosition())) {
                    if (oneShot && triggered)
                        return;
                    if (oneShot)
                        triggered = true;
                    if (destroy)
                        level.removeBlock(TriggerBlock.this);
                }
            }
        });
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public Block copy() {
        TriggerBlock block = (TriggerBlock)super.copy();
        block.destroy = destroy;
        block.oneShot = oneShot;
        return block;
    }
}
