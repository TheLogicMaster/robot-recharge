package com.thelogicmaster.robot_recharge.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.thelogicmaster.robot_recharge.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Todo: Better animation system
@NoArgsConstructor
public class Block implements Renderable3D, Disposable, AssetConsumer {
    @Getter @Setter private Position position;
    @Getter @Setter private boolean modeled;
    @Getter @Setter private boolean clean;
    @Getter @Setter private String asset;
    @Getter @Setter protected Array<ModelTextureAnimation> textureAnimations;
    @Getter @Setter private String animation;
    @Getter private float transparency;
    @Getter private Color color;
    @Getter @Setter private Direction direction = Direction.NORTH;

    protected transient ModelInstance model;
    protected transient Model modelSource;
    protected transient Level level;
    protected transient Quaternion rotation;
    protected transient AnimationController controller;
    private final transient Vector3 tempVec3 = new Vector3();

    public Block(Position position, Direction direction, boolean modeled, String asset, float transparency, Color color) {
        this.position = position;
        this.modeled = modeled;
        this.asset = asset;
        this.transparency = transparency;
        this.color = color;
        this.direction = direction;
    }

    public Block(Block block) {
        position = block.position;
        modeled = block.modeled;
        asset = block.asset;
        clean = block.clean;
        transparency = block.transparency;
        animation = block.animation;
        if (block.model != null) {
            model = new ModelInstance(block.model);
            controller = new AnimationController(model);
            if (animation != null)
                controller.setAnimation(animation, -1);
            if (block.textureAnimations != null) {
                textureAnimations = new Array<>();
                for (ModelTextureAnimation animation: block.textureAnimations) {
                    ModelTextureAnimation modelAnimation = new ModelTextureAnimation(animation);
                    modelAnimation.setup(model);
                    textureAnimations.add(modelAnimation);
                }
            }
        }
        color = block.color;
        direction = block.direction;
    }

    public Block copy() {
        return new Block(this);
    }

    public void setup(Level level) {
        // Todo: Reset animations
        this.level = level;
        rotation = direction.getQuaternion().cpy();
    }

    public boolean isSolid() {
        return true;
    }

    @Override
    public void loadAssets(AssetMultiplexer assetMultiplexer) {
        if (asset == null)
            return;
        if (modeled) // Ternary compressed version causes GWT compilation issue
            assetMultiplexer.load("blocks/" + asset, Model.class);
        else
            assetMultiplexer.load("blocks/" + asset, Texture.class);
        if (textureAnimations != null)
            for (ModelTextureAnimation animation: textureAnimations)
                animation.loadAssets(assetMultiplexer);
    }

    @Override
    public void assetsLoaded(AssetMultiplexer assetMultiplexer) {
        if (asset == null)
            return;
        if (modeled) {
            Model source = assetMultiplexer.get("blocks/" + asset);
            model = new ModelInstance(clean ? RobotUtils.cleanModel(source) : source);
        } else {
            modelSource = RobotUtils.createCubeModel(assetMultiplexer.get("blocks/" + asset));
            model = new ModelInstance(modelSource);
        }
        controller = new AnimationController(model);
        if (animation != null)
            controller.setAnimation(animation, -1);
        for (Material mat : new Array.ArrayIterator<>(model.materials)) {
            if (color != null)
                mat.set(ColorAttribute.createDiffuse(color));
            if (transparency > 0f)
                mat.set(new BlendingAttribute(1f - transparency));
        }
        if (textureAnimations != null)
            for (ModelTextureAnimation animation: textureAnimations) {
                animation.assetsLoaded(assetMultiplexer);
                animation.setup(model);
            }
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
        if (model != null)
            for (Material mat : new Array.ArrayIterator<>(model.materials))
                mat.set(new BlendingAttribute(1f - transparency));
    }

    public void setColor(Color color) {
        this.color = color;
        if (model != null)
            for (Material mat : new Array.ArrayIterator<>(model.materials))
                mat.set(ColorAttribute.createDiffuse(color));
    }

    @Override
    public void render(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment, float delta) {
        if (controller != null)
            controller.update(Gdx.graphics.getDeltaTime());
        if (model != null) {
            if (textureAnimations != null)
                for (ModelTextureAnimation animation: textureAnimations)
                    animation.update();
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
