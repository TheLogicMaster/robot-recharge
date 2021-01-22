package com.thelogicmaster.robot_recharge.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.thelogicmaster.robot_recharge.*;

public class TurnTable extends Block {

    private transient Level level;
    private transient float progress;
    private transient boolean clockWise, turning;

    private final static float speed = 2; // Turns/second

    public TurnTable() {
    }

    public TurnTable(Position position, Direction direction, boolean modeled, String asset, float transparency, Color color) {
        super(position, direction, modeled, asset, transparency, color);
    }

    public TurnTable(TurnTable turnTable) {
        super(turnTable);
    }

    @Override
    public TurnTable copy() {
        return new TurnTable(this);
    }

    @Override
    public void setup(Level level) {
        super.setup(level);
        this.level = level;
        level.addRobotListener(new RobotListenerAdaptor() {
            @Override
            public void onRobotMove(Robot robot) {
                if (robot.getBlockPos().equals(getPosition()) && robot.getDirection() != getDirection()) {
                    robot.setWaiting(true);
                    clockWise = (robot.getDirection().getYaw() + 90) % 360 == getDirection().getYaw();
                    turning = true;
                }
            }
        });
    }

    @Override
    public void render(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment, float delta) {
        super.render(modelBatch, decalBatch, environment, delta);

        if (!turning)
            return;
        progress += delta * speed;
        float robotAngle = level.getRobot().getDirection().getYaw();
        level.getRobot().getRotation().set(Vector3.Y, robotAngle + progress * (clockWise ? 90f : -90f));
        if (progress > 1f) {
            progress = 0;
            level.getRobot().setDirection(Direction.fromYaw(robotAngle + (clockWise ? 90 : -90)));
            if (level.getRobot().getDirection() == getDirection()) {
                level.getRobot().setWaiting(false);
                turning = false;
            }
        }
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
