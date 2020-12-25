package com.thelogicmaster.robot_recharge.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Vector3;
import com.thelogicmaster.robot_recharge.*;

public class TeleporterBlock extends Block {

    private Position target;
    private Direction direction;

    private transient ParticleEffect effectIn, effectOut;

    public TeleporterBlock() {
    }

    public TeleporterBlock(Position position, boolean cubic, String asset, Position target) {
        super(position, cubic, asset);
        this.target = target;
    }

    public TeleporterBlock(TeleporterBlock block) {
        super(block);
        target = block.target;
        effectIn = block.effectIn.copy();
        effectOut = block.effectOut.copy();
        direction = block.direction;
    }

    @Override
    public TeleporterBlock copy() {
        return new TeleporterBlock(this);
    }

    @Override
    public void setup(final Level level) {
        super.setup(level);
        level.addRobotListener(new RobotListenerAdaptor() {
            @Override
            public void onRobotMove(Robot robot) {
                if (robot.getBlockPos().equals(getPosition())) {
                    robot.setPosition(target.cpy());
                    if (direction != null)
                        robot.setDirection(direction);
                    level.playParticleEffect(effectIn, getPosition().toVector(new Vector3()).add(Constants.blockOffset));
                    level.playParticleEffect(effectOut, target.toVector(new Vector3()).add(Constants.blockOffset));
                }
            }
        });
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public void loadAssets(AssetManager assetManager) {
        super.loadAssets(assetManager);
        assetManager.load("teleportOut.pfx", ParticleEffect.class);
        assetManager.load("teleportIn.pfx", ParticleEffect.class);
    }

    @Override
    public void assetsLoaded(AssetManager assetManager) {
        super.assetsLoaded(assetManager);
        effectIn = assetManager.get("teleportIn.pfx");
        effectOut = assetManager.get("teleportOut.pfx");
    }
}
