package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.RobotUtils;
import com.thelogicmaster.robot_recharge.objectives.Objective;

public class LevelIntroDialog extends RobotDialog {

    public LevelIntroDialog(Skin skin, String level, Array<Objective> objectives, boolean useBlocks, final IntroListener listener) {
        super(level + " Objectives", skin);

        Table objectiveTab = new Table(skin);
        objectiveTab.setBackground("windowTen");
        for (Objective objective : objectives)
            objectiveTab.add(new Label(objective.getDescription(useBlocks), skin, "small")).padBottom(10).row();
        add(objectiveTab).padBottom(20).row();

        TextButton introCloseButton = new PaddedTextButton("Close", skin);
        introCloseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                setVisible(false);
                listener.onClose();
            }
        });
        add(introCloseButton).expand().bottom().padTop(50);
    }

    public interface IntroListener {

        void onClose();
    }
}
