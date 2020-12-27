package com.thelogicmaster.robot_recharge.blocks;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.*;

public class TriggerBlock extends Block {

    private boolean destroy;
    private boolean oneShot;
    private Color triggeredColor;
    private String name;

    private transient boolean triggered;

    public TriggerBlock() {
    }

    public TriggerBlock(Position position, boolean cubic, String asset, boolean destroy, boolean oneShot) {
        super(position, cubic, asset);
        this.destroy = destroy;
        this.oneShot = oneShot;
    }

    public TriggerBlock(TriggerBlock block) {
        super(block);
        destroy = block.destroy;
        oneShot = block.oneShot;
        triggeredColor = block.triggeredColor;
        name = block.name;
    }

    @Override
    public TriggerBlock copy() {
        return new TriggerBlock(this);
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
                    if (triggeredColor != null)
                        setColor(triggeredColor);
                    if (name != null)
                        level.emitLevelEvent(new LevelEvent(TriggerBlock.this, name, "trigger"));
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
}
