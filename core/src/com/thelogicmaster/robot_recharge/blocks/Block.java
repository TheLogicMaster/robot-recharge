package com.thelogicmaster.robot_recharge.blocks;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.thelogicmaster.robot_recharge.*;

public class Block implements Renderable3D, Disposable, AssetConsumer {
    private Position position;
    private boolean cubic;
    private String asset;

    protected transient ModelInstance model;
    protected transient Model modelSource;
    protected transient Level level;
    protected final transient Vector3 tempVec3 = new Vector3();

    public Block() {
        this(null);
    }

    public Block(String asset) {
        this(true, asset);
    }

    public Block(boolean cubic, String asset) {
        this(new Position(), cubic, asset);
    }

    public Block(Position position, boolean cubic, String asset) {
        this.position = position;
        this.cubic = cubic;
        if (asset != null)
            this.asset = "blocks/" + asset;
    }

    public Block copy() {
        Block block = new Block();
        block.position = position;
        block.cubic = cubic;
        block.asset = asset;
        if (model != null)
            block.model = new ModelInstance(model);
        block.level = level;
        return block;
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

    @Override
    public void loadAssets(AssetManager assetManager) {
        if (asset == null)
            return;
        if (cubic)
            assetManager.load(asset, Texture.class);
        else
            assetManager.load(asset, Model.class);
    }

    @Override
    public void assetsLoaded(AssetManager assetManager) {
        if (asset == null)
            return;
        if (cubic) {
            modelSource = RobotUtils.createCubeModel(assetManager.<Texture>get(asset));
            model = new ModelInstance(modelSource);
        } else
            model = new ModelInstance(assetManager.<Model>get(asset));
    }

    @Override
    public void render(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment, float delta) {
        if (model != null) {
            model.transform.setToTranslation(position.toVector(tempVec3).add(Constants.blockOffset));
            modelBatch.render(model, environment);
        }
    }

    @Override
    public void dispose() {
        if (modelSource != null)
            modelSource.dispose();
    }
}
