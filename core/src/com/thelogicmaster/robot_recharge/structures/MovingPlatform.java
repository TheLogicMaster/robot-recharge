package com.thelogicmaster.robot_recharge.structures;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.thelogicmaster.robot_recharge.*;
import com.thelogicmaster.robot_recharge.blocks.MovingPlatformBlock;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(exclude = {"platform", "platformIndicator", "tempVector1", "tempVector2"}, callSuper = true)
@NoArgsConstructor
public class MovingPlatform extends Structure {

    @Getter
    @Setter
    private int length;

    private transient volatile Position platformPosition;
    private transient ModelInstance platform;
    private transient Model platformIndicator;
    private transient boolean reverse, moving;
    private transient float progress;
    private final transient Vector3 tempVector1 = new Vector3(), tempVector2 = new Vector3();

    private static final float speed = 2f;

    public MovingPlatform(Position position, Direction direction, int length) {
        super(position, direction);
        this.length = length;
    }

    public Position getPlatformPosition() {
        return platformPosition;
    }

    @Override
    public void generate(final Level level) {
        super.generate(level);
        reverse = false;
        progress = 0;
        moving = false;
        platformPosition = position.cpy();
        level.addRobotListener(new RobotListenerAdaptor() {
            @Override
            public void onRobotMove(Robot robot) {
                if (!moving && robot.getBlockPos().equals(platformPosition.cpy().add(0, 1, 0))) {
                    moving = true;
                    level.getRobot().setWaiting(true);
                }
            }
        });
        for (int i = 0; i < length; i++)
            level.setBlock(new MovingPlatformBlock(this, new ModelInstance(platformIndicator)), position.cpy().add(direction, i));
    }

    @Override
    public void render(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment, float delta) {
        platformPosition.toVector(tempVector1);
        tempVector2.set(direction.getVector()).scl(reverse ? -progress : progress);
        tempVector1.add(Constants.blockOffset).add(tempVector2);
        platform.transform.setTranslation(tempVector1);

        if (moving) {
            progress += delta * speed;
            level.getRobot().getPosition().set(tempVector1.sub(Constants.blockOffset).add(0, 1, 0));
            if (progress >= 1) {
                progress = 0;
                platformPosition.add(direction, reverse ? -1 : 1);
                level.getRobot().setPosition(platformPosition.cpy().add(0, 1, 0));
                if (platformPosition.equals(position) || platformPosition.equals(position.cpy().add(direction, length - 1))) {
                    reverse = !reverse;
                    moving = false;
                    level.getRobot().setWaiting(false);
                }
            }
        }

        modelBatch.render(platform);
    }

    @Override
    public void loadAssets(AssetMultiplexer assetManager) {
        assetManager.load("Elevator.g3db", Model.class);
        assetManager.load("PlatformPosition.g3db", Model.class);
    }

    @Override
    public void assetsLoaded(AssetMultiplexer assetManager) {
        platform = new ModelInstance(RobotUtils.cleanModel(assetManager.get("Elevator.g3db")));
        platformIndicator = RobotUtils.cleanModel(assetManager.get("PlatformPosition.g3db"));
    }

    @Override
    public void dispose() {
    }
}
