package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.code.Solution;

public class SolutionsDialog extends RobotDialog {

    public SolutionsDialog(final Array<Solution> solutions, final SolutionsListener listener) {
        super("Solutions");

        final IterativeStack descriptionStack = new IterativeStack();
        for (Solution solution : solutions) {
            Label label = new Label(solution.getDescription(), getSkin(), "small");
            label.setWrap(true);
            descriptionStack.add(label);
        }

        final List<Solution> list = new List<>(getSkin());
        list.setItems(solutions);
        add(list).padRight(20);

        add(descriptionStack).grow().row();

        TextButton closeButton = new PaddedTextButton("Close", getSkin());
        add(closeButton).bottom().left();

        TextButton loadButton = new PaddedTextButton("Load Solution", getSkin());
        add(loadButton).expandY().bottom();

        list.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                descriptionStack.show(list.getSelectedIndex());
            }
        });
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                listener.onClose();
                setVisible(false);
            }
        });
        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                listener.onLoad(list.getSelected());
                listener.onClose();
                setVisible(false);
            }
        });
    }

    public interface SolutionsListener {
        void onClose();

        void onLoad(Solution solution);
    }
}
