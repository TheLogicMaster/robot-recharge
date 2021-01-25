package com.thelogicmaster.robot_recharge.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Vector3;
import com.thelogicmaster.robot_recharge.*;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = {"effectIn", "effectOut"})
public class Teleporter extends Block {

    private Position target;
    private Direction face;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private transient ParticleEffect effectIn, effectOut;

    public Teleporter(Position position, Direction direction, boolean cubic, String asset, float transparency, Color color, Position target, Direction face) {
        super(position, direction, cubic, asset, transparency, color);
        this.target = target;
        this.face = face;
    }

    public Teleporter(Teleporter block) {
        super(block);
        target = block.target;
        effectIn = block.effectIn.copy();
        effectOut = block.effectOut.copy();
        face = block.face;
    }

    @Override
    public Teleporter copy() {
        return new Teleporter(this);
    }

    @Override
    public void setup(final Level level) {
        super.setup(level);
        level.addRobotListener(new RobotListenerAdaptor() {
            @Override
            public void onRobotMove(Robot robot) {
                if (robot.getBlockPos().equals(getPosition())) {
                    robot.setPosition(target.cpy());
                    if (face != null)
                        robot.setDirection(face);
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
    public void loadAssets(AssetMultiplexer assetManager) {
        super.loadAssets(assetManager);
        assetManager.load("teleportOut.pfx", ParticleEffect.class);
        assetManager.load("teleportIn.pfx", ParticleEffect.class);
    }

    @Override
    public void assetsLoaded(AssetMultiplexer assetManager) {
        super.assetsLoaded(assetManager);
        effectIn = assetManager.get("teleportIn.pfx");
        effectOut = assetManager.get("teleportOut.pfx");
    }
}
