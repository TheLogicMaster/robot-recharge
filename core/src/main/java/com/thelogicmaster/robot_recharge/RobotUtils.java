package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thelogicmaster.robot_recharge.blocks.*;
import com.thelogicmaster.robot_recharge.objectives.EventCountObjective;
import com.thelogicmaster.robot_recharge.objectives.EventsObjective;
import com.thelogicmaster.robot_recharge.objectives.MaxLengthObjective;
import com.thelogicmaster.robot_recharge.objectives.MaxTimeObjective;
import com.thelogicmaster.robot_recharge.structures.BlocksStructure;
import com.thelogicmaster.robot_recharge.structures.MovingPlatform;
import com.thelogicmaster.robot_recharge.structures.Wire;

public class RobotUtils {

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
            mat.remove(ColorAttribute.Emissive);
            mat.remove(BlendingAttribute.Type);
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
        TextureRegion[] textureRegions = TextureRegion.split(texture, texture.getHeight(), texture.getHeight())[0];
        builder.begin();
        Material material = new Material("box");
        material.set(TextureAttribute.createDiffuse(texture));
        MeshPartBuilder box = builder.part("box", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, material);
        box.setUVRange(textureRegions[0]);
        box.rect(-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0f, 1f, 0f);
        box.setUVRange(textureRegions[1]);
        box.rect(-0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0f, -1f, 0f);
        box.setUVRange(textureRegions[2]);
        box.rect(0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 1f, 0f, 0f);
        box.setUVRange(textureRegions[3]);
        box.rect(-0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0f, 0f, 1f);
        box.setUVRange(textureRegions[4]);
        box.rect(-0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -1f, 0f, 0f);
        box.setUVRange(textureRegions[5]);
        box.rect(0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0f, 0f, -1f);
        return builder.end();
    }

    public static Json createJson() {
        Json json = new Json();
        json.addClassTag("BlockStructure", BlocksStructure.class);
        json.addClassTag("MovingPlatform", MovingPlatform.class);
        json.addClassTag("Wire", Wire.class);
        json.addClassTag("Trigger", Trigger.class);
        json.addClassTag("PressurePlate", PressurePlate.class);
        json.addClassTag("Teleporter", Teleporter.class);
        json.addClassTag("Charger", Charger.class);
        json.addClassTag("TurnTable", TurnTable.class);
        json.addClassTag("EventsObjective", EventsObjective.class);
        json.addClassTag("EventCountObjective", EventCountObjective.class);
        json.addClassTag("MaxLengthObjective", MaxLengthObjective.class);
        json.addClassTag("MaxTimeObjective", MaxTimeObjective.class);
        return json;
    }

    public static int getLevelIndex(String level) {
        for (int i = 0; i < RobotRecharge.assets.levelInfo.size; i++)
            if (RobotRecharge.assets.levelInfo.get(i).getName().equals(level))
                return i;
        return -1;
    }

    public static void playSoundEffect(Sound sound) {
        playSoundEffect(sound, 1f);
    }

    public static void playSoundEffect(Sound sound, float volume) {
        sound.play(volume * RobotRecharge.prefs.getEffectsVolume());
    }

    public static void playNavigationSound() {
        playSoundEffect(RobotRecharge.assets.navigateSound);
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
                sound.play(RobotRecharge.prefs.getEffectsVolume());
            return sound;
        }
        return null;
    }

    public static boolean usesGameJolt() {
        return Gdx.app.getType() == Application.ApplicationType.Desktop
                || Gdx.app.getType() == Application.ApplicationType.WebGL
                || Gdx.app.getType() == Application.ApplicationType.Applet;
    }

    public static void restorePurchases() {
        RobotRecharge.gameServices.restorePurchases(purchases -> RobotRecharge.prefs.restorePurchases(purchases));
    }

    public static void loadCloudSave() {
        RobotRecharge.gameServices.loadGameState("save", gameState -> {
            if (gameState != null)
                RobotRecharge.prefs.loadGameSave(RobotAssets.json.fromJson(GameSave.class, Base64Coder.decodeString(new String(gameState))));
        });
    }

    public static void padDrawable(Drawable drawable, float padding) {
        drawable.setLeftWidth(padding);
        drawable.setRightWidth(padding);
        drawable.setTopHeight(padding);
        drawable.setBottomHeight(padding);
    }
}
