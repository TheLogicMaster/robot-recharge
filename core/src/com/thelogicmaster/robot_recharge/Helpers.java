package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Helpers {

    public static Viewport createViewport(Camera camera) {
        return new FitViewport(Constants.worldWidth, Constants.worldHeight, camera);
    }

    /**
     * Removes opacity and emissive properties from model to fix white textures and invisible models
     * @param model
     */
    public static void cleanModel(Model model) {
        for (Material mat: new Array.ArrayIterator<>(model.materials)) {
            mat.set(new BlendingAttribute(1));
            mat.remove(ColorAttribute.Emissive);
        }
    }

    /**
     * Hides and disables an actor
     * @param actor to hide
     * @param hide or not
     */
    public static void hideActor(Actor actor, boolean hide) {
        actor.setTouchable(hide ? Touchable.disabled : Touchable.enabled);
        actor.setVisible(!hide);
        if (actor instanceof Disableable)
            ((Disableable)actor).setDisabled(hide);
    }
}
