package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.RobotUtils;
import com.thelogicmaster.robot_recharge.TutorialPage;

public class TutorialDialog extends Dialog {

    private final IterativeStack stack;
    private final TextButton nextButton;
    private final TextButton backButton;

    public TutorialDialog(Skin skin, Array<TutorialPage> tutorial) {
        super("Tutorial", skin);
        stack = new IterativeStack();
        for (TutorialPage page : new Array.ArrayIterator<>(tutorial)) {
            Table table = new Table();
            table.add(new Image(new TextureRegionDrawable(new TextureRegion(new Texture("tutorial/" + page.getImage()))))).row();
            table.add(new Label(page.getText(), skin)).fillX();
            stack.add(table);
        }
        padTop(50);
        setMovable(false);
        backButton = new TextButton("Back", skin);
        nextButton = new TextButton("Next", skin);
        TextButton skipButton = new TextButton("Skip", skin);
        getButtonTable().add(backButton).padRight(20);
        getButtonTable().add(nextButton).padRight(20);
        getButtonTable().add(skipButton);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                stack.previous();
            }
        });
        nextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                stack.next();
            }
        });
        skipButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                hide();
            }
        });
        updateButtons();
    }

    private void updateButtons() {
        backButton.setDisabled(stack.getIndex() == 0);
        nextButton.setDisabled(stack.getIndex() != 0);
    }

    public void reset() {
        stack.show(0);
        updateButtons();
    }
}
