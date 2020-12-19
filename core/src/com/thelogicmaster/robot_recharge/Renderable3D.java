package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;

/**
 * Represents an object that uses a ModelBatch to render
 */
public interface Renderable3D {

    void render(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment, float delta);
}
