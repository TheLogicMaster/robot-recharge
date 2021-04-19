package com.thelogicmaster.robot_recharge.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.thelogicmaster.robot_recharge.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Charger extends Block {

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
    public void assetsLoaded (AssetMultiplexer assetMultiplexer) {
        super.assetsLoaded(assetMultiplexer);

        model.getMaterial("base").set(new ColorAttribute(ColorAttribute.AmbientLight, Color.RED));
    }

    @Override
    public void setup(final Level level) {
        super.setup(level);
        level.addRobotListener(new RobotListenerAdaptor() {
            @Override
            public void onRobotMove(Robot robot) {
                if (robot.getBlockPos().equals(getPosition())) {
                    if (textureAnimations != null && textureAnimations.size > 0)
                        textureAnimations.get(0).setSpeed(textureAnimations.get(0).getSpeed() * 3);
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
