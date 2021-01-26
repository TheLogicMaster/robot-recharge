package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.*;
import com.thelogicmaster.robot_recharge.ui.LanguageSelect;
import com.thelogicmaster.robot_recharge.ui.PaddedTextButton;

public class TutorialsScreen extends MenuScreen {

    public TutorialsScreen(RobotScreen previousScreen) {
        super(previousScreen);

        Table tutorialsTable = new Table(skin);
        tutorialsTable.setBackground("secondaryPanel");
        tutorialsTable.setBounds(uiViewport.getWorldWidth() / 2 - 400, uiViewport.getWorldHeight() / 2 - 300, 800, 600);

        final List<LevelInfo> list = new List<>(skin);
        list.setItems(RobotAssets.json.fromJson(Array.class, LevelInfo.class, Gdx.files.internal("tutorials.json")));
        tutorialsTable.add(list).padBottom(20).row();

        final LanguageSelect languageSelect = new LanguageSelect(skin);
        tutorialsTable.add(languageSelect).padBottom(10).row();

        final CheckBox blocklyCheckbox = new CheckBox("Use Blockly", skin);
        tutorialsTable.add(blocklyCheckbox).padBottom(10).row();

        TextButton loadButton = new PaddedTextButton("Load Tutorial", skin);
        tutorialsTable.add(loadButton);

        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                LevelSave save = new LevelSave(blocklyCheckbox.isChecked(), "", list.getSelected().getName(), languageSelect.getSelected());
                RobotRecharge.instance.setScreen(new GameScreen(save));
                RobotRecharge.assets.titleMusic.stop();
            }
        });

        stage.addActor(tutorialsTable);
    }
}
