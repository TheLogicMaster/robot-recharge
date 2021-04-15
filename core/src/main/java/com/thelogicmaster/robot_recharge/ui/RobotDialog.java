package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.thelogicmaster.robot_recharge.RobotRecharge;

/**
 * A modal Window that is padded correctly and isn't movable
 */
public class RobotDialog extends Window {

    private static final float FADE_TIME = 0.3f;

    public RobotDialog(String title) {
        super(title, RobotRecharge.assets.skin);
        setup();
    }

    public RobotDialog(String title, String styleName) {
        super(title, RobotRecharge.assets.skin, styleName);
        setup();
    }

    private void setup() {
        setModal(true);
        setMovable(false);
        setVisible(false);
        padTop(100);
    }

    public void show(Stage stage) {
        stage.addActor(this);
        pack();
        setPosition((stage.getWidth() - getWidth()) / 2, (stage.getHeight() - getHeight()) / 2);
        setVisible(true);
        setTouchable(Touchable.enabled);
        setColor(1, 1, 1, 0);
        addAction(Actions.fadeIn(FADE_TIME, Interpolation.fade));
    }

    public void hide() {
        setTouchable(Touchable.disabled);
        addAction(Actions.sequence(Actions.fadeOut(FADE_TIME, Interpolation.fade), new Action() {
            @Override
            public boolean act (float delta) {
                setVisible(false);
                remove();
                return true;
            }
        }));
    }
}
