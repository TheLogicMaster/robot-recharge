package com.thelogicmaster.robot_recharge.structures;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.Level;
import com.thelogicmaster.robot_recharge.blocks.Block;

public interface IStructure {

    void generate(Level level);

    void render(ModelBatch batch, Environment environment, float delta);

    void loadAssets(AssetManager assetManager);

    void assetsLoaded(AssetManager assetManager);

    void dispose();
}
