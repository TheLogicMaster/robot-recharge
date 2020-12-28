package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.objectives.Objective;

public class LevelFailDialog extends Window {

    private final Array<Label> labels = new Array<>();
    private final boolean useBlocks;

    public LevelFailDialog(Skin skin, Array<Objective> objectives, boolean useBlocks) {
        super("Incomplete Objectives", skin);
        this.useBlocks = useBlocks;
        padTop(100);
        setMovable(false);
        setModal(true);
        setVisible(false);
        Table table = new Table(skin);
        table.setBackground("windowTen");
        for (Objective objective : objectives) {
            Label label = new Label(objective.getDescription(useBlocks), skin);
            table.add(label).row();
            labels.add(label);
        }
        add(table).row();
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setVisible(false);
            }
        });
        add(closeButton).expand().right().bottom();
    }

    public void show(Array<Objective> failed) {
        setVisible(true);
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
