package com.thelogicmaster.robot_recharge.blocks;

import com.badlogic.gdx.graphics.Color;
import com.thelogicmaster.robot_recharge.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Trigger extends Block {

    private boolean destroy;
    private boolean oneShot;
    private Color triggeredColor;
    private String name;

    @Setter(AccessLevel.NONE)
    private transient boolean triggered;

    public Trigger(Position position, Direction direction, boolean cubic, String asset, float transparency, Color color, boolean destroy, boolean oneShot) {
        super(position, direction, cubic, asset, transparency, color);
        this.destroy = destroy;
        this.oneShot = oneShot;
    }

    public Trigger(Trigger block) {
        super(block);
        destroy = block.destroy;
        oneShot = block.oneShot;
        triggeredColor = block.triggeredColor;
        name = block.name;
    }

    @Override
    public Trigger copy() {
        return new Trigger(this);
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
                        level.emitLevelEvent(new LevelEvent(Trigger.this, name, "trigger"));
                    if (oneShot)
                        triggered = true;
                    if (destroy)
                        level.removeBlock(Trigger.this);
                }
            }
        });
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
