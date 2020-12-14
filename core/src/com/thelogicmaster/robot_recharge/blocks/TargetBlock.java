package com.thelogicmaster.robot_recharge.blocks;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.utils.Array;

public class TargetBlock extends Block {

    @Override
    public void assetsLoaded(AssetManager assetManager) {
        super.assetsLoaded(assetManager);
        if (modelSource != null)
            for (Material mat : new Array.ArrayIterator<>(modelSource.materials))
                mat.set(new BlendingAttribute(0.7f));
    }

    @Override
    public void render(ModelBatch batch, Environment environment, float delta) {

        super.render(batch, environment, delta);
    }
}
