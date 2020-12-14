package com.thelogicmaster.robot_recharge.structures;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Disposable;
import com.thelogicmaster.robot_recharge.AssetConsumer;
import com.thelogicmaster.robot_recharge.ModelRenderable;
import com.thelogicmaster.robot_recharge.Level;
import com.thelogicmaster.robot_recharge.Position;

public abstract class Structure implements AssetConsumer, ModelRenderable, Disposable {

    private transient Level level;

    protected Position position;
    protected int rotation;

    public Structure(Position position, int rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    @Override
    public void render(ModelBatch batch, Environment environment, float delta) {

    }

    /**
     * Generates all of the blocks for the structure
     * @param level to place blocks
     */
    public void generate(Level level) {
        this.level = level;
    }

    /**
     * Translates a Position from relative coordinates and by the rotation
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

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
}
