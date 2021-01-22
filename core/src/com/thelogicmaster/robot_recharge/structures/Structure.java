package com.thelogicmaster.robot_recharge.structures;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.utils.Disposable;
import com.thelogicmaster.robot_recharge.*;

public abstract class Structure implements AssetConsumer, Renderable3D, Disposable {

    protected transient Level level;

    protected Position position;
    protected Direction direction;

    public Structure() {
    }

    public Structure(Position position, Direction direction) {
        this.position = position;
        this.direction = direction;
    }

    @Override
    public void render(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment, float delta) {

    }

    /**
     * Generates all of the blocks for the structure
     *
     * @param level to place blocks
     */
    public void generate(Level level) {
        this.level = level;
    }

    /**
     * Translates a Position from relative coordinates and by the rotation
     *
     * @param position to transform
     * @return transformed position
     */
    protected Position transformPosition(Position position) {
        // Todo: Transform based on position and rotation
        return position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
