package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.RobotUtils;
import com.thelogicmaster.robot_recharge.objectives.Objective;

public class LevelIncompleteDialog extends RobotDialog {

    private final Array<Label> labels = new Array<>();
    private final boolean useBlocks;

    public LevelIncompleteDialog(Array<Objective> objectives, boolean useBlocks) {
        super("Incomplete Objectives");

        this.useBlocks = useBlocks;

        Table table = new Table(getSkin());
        table.setBackground("windowTen");
        for (Objective objective : objectives) {
            Label label = new Label(objective.getDescription(useBlocks), getSkin(), "small");
            table.add(label).row();
            labels.add(label);
        }
        add(table).row();
        TextButton closeButton = new PaddedTextButton("Close", getSkin());
        add(closeButton).expand().right().bottom();

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                setVisible(false);
            }
        });
    }

    public void show(Stage stage, Array<Objective> failed) {
        show(stage);

        for (Label label : labels) {
            boolean fail = false;
            for (Objective objective : failed) {
                if (label.getText().toString().equals(objective.getDescription(useBlocks))) {
                    fail = true;
                    break;
                }
            }
            Label.LabelStyle style = new Label.LabelStyle(label.getStyle());
            style.fontColor = fail ? Color.RED : Color.GREEN;
            label.setStyle(style);
        }
    }
}
