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
public class PressurePlate extends Block {
    private String id;

    @Setter(AccessLevel.NONE)
    private transient boolean triggered;

    public PressurePlate(Position position, Direction direction, boolean cubic, String asset, float transparency, Color color, String id) {
        super(position, direction, cubic, asset, transparency, color);
        this.id = id;
    }

    public PressurePlate(PressurePlate block) {
        super(block);
        id = block.id;
    }

    @Override
    public PressurePlate copy() {
        return new PressurePlate(this);
    }

    @Override
    public void setup(final Level level) {
        super.setup(level);
        level.addRobotListener(new RobotListenerAdaptor() {
            @Override
            public void onRobotMove(Robot robot) {
                if (id == null)
                    return;
                if (robot.getBlockPos().equals(getPosition()))
                    level.emitLevelEvent(new LevelEvent(PressurePlate.this, id, "on"));
                else if (robot.getBlockPos().cpy().add(robot.getDirection(), -1).equals(getPosition()))
                    level.emitLevelEvent(new LevelEvent(PressurePlate.this, id, "off"));
            }
        });
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
