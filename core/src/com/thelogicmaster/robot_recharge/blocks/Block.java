package com.thelogicmaster.robot_recharge.blocks;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.thelogicmaster.robot_recharge.*;

public class Block implements Renderable3D, Disposable, AssetConsumer {
    private Position position;
    private boolean modeled;
    private String asset;
    private float transparency;
    private Color color;
    private Direction direction = Direction.NORTH;

    protected transient ModelInstance model;
    protected transient Model modelSource;
    protected transient Level level;
    protected transient Quaternion rotation;
    private final transient Vector3 tempVec3 = new Vector3();

    public Block() {
    }

    public Block(Position position, Direction direction, boolean modeled, String asset, float transparency, Color color) {
        this.position = position;
        this.direction = direction;
        this.modeled = modeled;
        this.asset = asset;
        this.transparency = transparency;
        this.color = color;
    }

    public Block(Block block) {
        position = block.position;
        modeled = block.modeled;
        asset = block.asset;
        transparency = block.transparency;
        if (block.model != null)
            model = new ModelInstance(block.model);
        color = block.color;
        direction = block.direction;
    }

    public Block copy() {
        return new Block(this);
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isModeled() {
        return modeled;
    }

    public void setModeled(boolean modeled) {
        this.modeled = modeled;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public void setup(Level level) {
        this.level = level;
        rotation = direction.getQuaternion().cpy();
    }

    public boolean isSolid() {
        return true;
    }

    @Override
    public void loadAssets(AssetManager assetManager) {
        if (asset == null)
            return;
        if (modeled) // Ternary compressed version causes GWT compilation issue
            assetManager.load("blocks/" + asset,  Model.class);
        else
            assetManager.load("blocks/" + asset, Texture.class);
    }

    @Override
    public void assetsLoaded(AssetManager assetManager) {
        if (asset == null)
            return;
        if (modeled)
            model = new ModelInstance(assetManager.<Model>get("blocks/" + asset));
        else {
            modelSource = RobotUtils.createCubeModel(assetManager.<Texture>get("blocks/" + asset));
            model = new ModelInstance(modelSource);
        }
        for (Material mat : new Array.ArrayIterator<>(model.materials)) {
            if (color != null)
                mat.set(ColorAttribute.createDiffuse(color));
            if (transparency > 0f)
                mat.set(new BlendingAttribute(1 - transparency));
        }
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
        if (model != null)
            for (Material mat : new Array.ArrayIterator<>(model.materials))
                mat.set(new BlendingAttribute(1 - transparency));
    }

    public void setColor(Color color) {
        this.color = color;
        if (model != null)
            for (Material mat : new Array.ArrayIterator<>(model.materials))
                mat.set(ColorAttribute.createDiffuse(color));
    }

    @Override
    public void render(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment, float delta) {
        if (model != null) {
            model.transform.set(position.toVector(tempVec3).add(Constants.blockOffset), rotation);
            modelBatch.render(model, environment);
        }
    }

    @Override
    public void dispose() {
        if (modelSource != null)
            modelSource.dispose();
    }
}
