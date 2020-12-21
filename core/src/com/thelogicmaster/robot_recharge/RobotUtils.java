package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thelogicmaster.robot_recharge.blocks.TriggerBlock;
import com.thelogicmaster.robot_recharge.structures.BlocksStructure;
import com.thelogicmaster.robot_recharge.structures.Elevator;

public class RobotUtils {

    public static final Json json = createJson();

    public static Viewport createViewport(Camera camera) {
        return new FitViewport(Constants.worldWidth, Constants.worldHeight, camera);
    }

    public static AssetManager createAssetManager() {
        AssetManager assetManager = new AssetManager();
        //assetManager.setLoader(SceneAsset.class, ".gltf", new GLTFAssetLoader());
        //assetManager.setLoader(SceneAsset.class, ".glb", new GLBAssetLoader());
        return assetManager;
    }

    /**
     * Removes opacity and emissive properties from model to fix white textures and invisible models
     *
     * @param model
     */
    public static Model cleanModel(Model model) {
        for (Material mat : new Array.ArrayIterator<>(model.materials)) {
            mat.set(new BlendingAttribute(1));
            mat.remove(ColorAttribute.Emissive);
        }
        return model;
    }

    /**
     * Hides and disables an actor
     *
     * @param actor to hide
     * @param hide  or not
     */
    public static void hideActor(Actor actor, boolean hide) {
        actor.setTouchable(hide ? Touchable.disabled : Touchable.enabled);
        actor.setVisible(!hide);
        if (actor instanceof Disableable)
            ((Disableable) actor).setDisabled(hide);
    }

    public static Model createCubeModel(Texture texture) {
        ModelBuilder builder = new ModelBuilder();
        int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        Array<Attribute> attributes = new Array<>(new Attribute[]{new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA),
                IntAttribute.createCullFace(GL20.GL_NONE), new DepthTestAttribute(false)});
        TextureRegion[] textureRegions = TextureRegion.split(texture, texture.getHeight(), texture.getHeight())[0];
        builder.begin();
        Material material = new Material(attributes);
        material.set(TextureAttribute.createDiffuse(textureRegions[0]));
        builder.part("box", GL20.GL_TRIANGLES, attr, material)
                .rect(-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0f, 1f, 0f);
        material = new Material(attributes);
        material.set(TextureAttribute.createDiffuse(textureRegions[1]));
        builder.part("box", GL20.GL_TRIANGLES, attr, material)
                .rect(-0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0f, -1f, 0f);
        material = new Material(attributes);
        material.set(TextureAttribute.createDiffuse(textureRegions[2]));
        builder.part("box", GL20.GL_TRIANGLES, attr, material)
                .rect(0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 1f, 0f, 0f);
        material = new Material(attributes);
        material.set(TextureAttribute.createDiffuse(textureRegions[3]));
        builder.part("box", GL20.GL_TRIANGLES, attr, material)
                .rect(-0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0f, 0f, 1f);
        material = new Material(attributes);
        material.set(TextureAttribute.createDiffuse(textureRegions[4]));
        builder.part("box", GL20.GL_TRIANGLES, attr, material)
                .rect(-0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -1f, 0f, 0f);
        material = new Material(attributes);
        material.set(TextureAttribute.createDiffuse(textureRegions[5]));
        builder.part("box", GL20.GL_TRIANGLES, attr, material)
                .rect(0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0f, 0f, -1f);
        return builder.end();
    }

    public static Json createJson() {
        Json json = new Json();
        json.addClassTag("BlockStructure", BlocksStructure.class);
        json.addClassTag("Elevator", Elevator.class);
        json.addClassTag("TriggerBlock", TriggerBlock.class);
        return json;
    }

    /**
     * Actual modulus implementation, not remainder operator
     * Source: https://stackoverflow.com/a/4412200
     *
     * @param a The dividend
     * @param b The divisor
     * @return modulus result
     */
    public static int modulus(int a, int b) {
        return (a % b + b) % b;
    }

    /**
     * Converts text to Sound and plays it if TTS is available
     *
     * @param text The text to speak
     * @return The generated Sound
     */
    public static Sound textToSpeech(String text) {
        if (RobotRecharge.ttsEngine != null) {
            Sound sound = RobotRecharge.ttsEngine.textToSpeech(text);
            if (sound != null)
                sound.play();
            return sound;
        }
        return null;
    }
}
