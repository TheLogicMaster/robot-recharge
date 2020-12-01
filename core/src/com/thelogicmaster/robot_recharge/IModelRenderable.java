package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

/**
 * Represents an object that uses a ModelBatch to render
 */
public interface IModelRenderable {

    void render(ModelBatch batch, Environment environment, float delta);
}
