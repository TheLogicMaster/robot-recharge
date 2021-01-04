package com.thelogicmaster.robot_recharge.blocks;

import com.badlogic.gdx.graphics.Color;
import com.thelogicmaster.robot_recharge.*;

public class Charger extends Block {

    public Charger() {
    }

    public Charger(Position position, Direction direction, boolean modeled, String asset, float transparency, Color color) {
        super(position, direction, modeled, asset, transparency, color);
    }

    public Charger(Charger charger) {
        super(charger);
    }

    @Override
    public Charger copy() {
        return new Charger(this);
    }

    @Override
    public void setup(final Level level) {
        super.setup(level);
        level.addRobotListener(new RobotListenerAdaptor() {
            @Override
            public void onRobotMove(Robot robot) {
                if (robot.getBlockPos().equals(getPosition())) {
                    level.emitLevelEvent(new LevelEvent(Charger.this, "charger", "charge"));
                    level.completeLevel();
                }
            }
        });
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
