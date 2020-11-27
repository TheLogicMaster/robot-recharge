package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;

public class IterativeStack extends Stack {

    private int index;

    public IterativeStack() {
        super();
        showSelected();
    }

    public IterativeStack(Actor... actors) {
        super(actors);
    }

    private void showSelected() {
        for (int i = 0; i < getChildren().size; i++)
            Helpers.hideActor(getChildren().get(i), index != i);
    }

    public void next() {
        index++;
        index %= getChildren().size;
        showSelected();
    }

    public void show(int index) {
        this.index = index;
        showSelected();
    }

    @Override
    protected void childrenChanged() {
        super.childrenChanged();
        index = Math.min(index, getChildren().size - 1);
        showSelected();
    }
}
