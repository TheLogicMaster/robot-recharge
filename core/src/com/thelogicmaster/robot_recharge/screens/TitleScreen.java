package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class TitleScreen extends RobotScreen {

    public TitleScreen() {
        Table table = new Table(menuSkin);
        table.setBounds(uiViewport.getWorldWidth() / 2 - 400, 20, 800, uiViewport.getWorldHeight() - 40);
        table.debug(Table.Debug.all);
        table.setBackground("titleMenu");
        stage.addActor(table);
        setBackground(new Texture("titleBackground.png"));
    }
}
