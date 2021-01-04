package com.thelogicmaster.robot_recharge.structures;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.thelogicmaster.robot_recharge.*;
import com.thelogicmaster.robot_recharge.blocks.ElevatorBlock;

public class Elevator extends Structure {

    private int height;

    private transient volatile int floor;
    private transient ModelInstance elevator;
    private transient boolean goingDown, moving;
    private transient float progress;
    private transient Level level;
    private final transient Vector3 tempVector = new Vector3();

    private static final float speed = 2f;

    public Elevator() {
    }

    public Elevator(Position position, int rotation, int height) {
        super(position, rotation);
        this.height = height;
    }

    public int getFloor() {
        return floor;
    }

    @Override
    public void generate(final Level level) {
        this.level = level;
        goingDown = false;
        progress = 0;
        moving = false;
        floor = position.y;
        level.addRobotListener(new RobotListenerAdaptor() {
            @Override
            public void onRobotMove(Robot robot) {
                Position pos = robot.getBlockPos();
                if (!moving && pos.x == position.x && pos.z == position.z && (pos.y == position.y || pos.y == position.y + height)) {
                    moving = true;
                    level.getRobot().setWaiting(true);
                }
            }
        });
        for (int i = 0; i < height; i++)
            level.setBlock(new ElevatorBlock(this), transformPosition(new Position(position.x, position.y + i, position.z)));
    }

    private synchronized void nextFloor() {
        floor += goingDown ? -1 : 1;
    }

    public void render(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment, float delta) {
        if (moving) {
            progress += delta * speed;
            level.getRobot().getPosition().y = floor + (goingDown ? -progress : progress);
            if (progress > 1) {
                progress = 0;
                nextFloor();
                Position robotPos = position.cpy();
                robotPos.y = floor;
                level.getRobot().setPosition(robotPos);
                if (floor == (goingDown ? position.y : position.y + height)) {
                    goingDown = !goingDown;
                    moving = false;
                    level.getRobot().setWaiting(false);
                }
            }
        }

        position.toVector(tempVector).y = floor;
        tempVector.add(Constants.blockOffset).add(0, -1 + (goingDown ? -progress : progress), 0);
        elevator.transform.setTranslation(tempVector);
        modelBatch.render(elevator);
    }

    @Override
    public void loadAssets(AssetManager assetManager) {
        assetManager.load("Elevator.g3db", Model.class);
    }

    @Override
    public void assetsLoaded(AssetManager assetManager) {
        elevator = new ModelInstance(RobotUtils.cleanModel(assetManager.<Model>get("Elevator.g3db")));
    }

    @Override
    public void dispose() {
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
