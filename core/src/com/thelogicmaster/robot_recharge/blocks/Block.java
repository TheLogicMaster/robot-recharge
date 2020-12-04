package com.thelogicmaster.robot_recharge.blocks;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.thelogicmaster.robot_recharge.*;

public class Block implements IModelRenderable {
    private Position position;
    private boolean cubic;
    private String asset;

    protected transient ModelInstance model;
    protected transient Model modelSource;
    protected transient Level level;
    protected final transient Vector3 tempVec3 = new Vector3();

    public Block() {
    }

    public Block(String asset) {
        this(new Position(), false, asset);
    }

    public Block(boolean cubic, String asset) {
        this(new Position(), cubic, asset);
    }

    public Block(Position position, boolean cubic, String asset) {
        this.position = position;
        this.cubic = cubic;
        this.asset = asset;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isCubic() {
        return cubic;
    }

    public void setCubic(boolean cubic) {
        this.cubic = cubic;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public void setup(Level level) {
        this.level = level;
    }

    public boolean isSolid() {
        return true;
    }

    void loadAssets(AssetManager assetManager) {
        if (asset == null)
            return;
        if (cubic)
            assetManager.load(asset, Texture.class);
        else
            assetManager.load(asset, Model.class);
    }

    void assetsLoaded(AssetManager assetManager) {
        if (asset == null)
            return;
        if (cubic)
            model = new ModelInstance(Helpers.createCubeModel(assetManager.<Texture>get(asset)));
        else {
            modelSource = assetManager.get(asset);
            model = new ModelInstance(modelSource);
        }
    }

    @Override
    public void render(ModelBatch batch, Environment environment, float delta) {
        if (model != null) {
            model.transform.setToTranslation(position.toVector(tempVec3).add(Constants.blockOffset));
            batch.render(model, environment);
        }
    }

    public void dispose() {
        if (modelSource != null)
            modelSource.dispose();
    }
}