package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.LevelInfo;
import com.thelogicmaster.robot_recharge.LevelSave;
import com.thelogicmaster.robot_recharge.RobotRecharge;
import com.thelogicmaster.robot_recharge.RobotUtils;
import com.thelogicmaster.robot_recharge.code.Language;
import com.thelogicmaster.robot_recharge.ui.IterativeStack;

public class LevelScreen extends MenuScreen {

    public LevelScreen(RobotScreen previousScreen) {
        super(previousScreen);

        final Array<LevelInfo> levels = RobotUtils.json.fromJson(Array.class, LevelInfo.class, Gdx.files.internal("levels.json"));

        // Level description
        final com.thelogicmaster.robot_recharge.ui.IterativeStack stack = new IterativeStack();
        for (LevelInfo level : new Array.ArrayIterator<>(levels)) {
            Table table = new Table(skin);
            table.setBackground("buttonTen");
            Label levelInfo = new Label("", skin);
            levelInfo.setWrap(true);
            levelInfo.setText(level.getDescription());
            table.pad(10);
            table.align(Align.top);
            table.add(levelInfo).growX();
            stack.add(table);
        }
        stack.setBounds(uiViewport.getWorldWidth() - 1000, 50, 800, 400);
        stage.addActor(stack);

        // Controls
        Table controlsTable = new Table(skin);
        controlsTable.setBackground("buttonTen");
        controlsTable.setBounds(uiViewport.getWorldWidth() - 1000, uiViewport.getWorldHeight() - 590, 800, 350);
        final SelectBox<Language> languageSelect = new SelectBox<>(skin);
        Array<Language> languages = new Array<>();
        for (Language language : Language.values())
            if (RobotRecharge.codeEngines.containsKey(language))
                languages.add(language);
        languageSelect.setItems(languages);
        controlsTable.add(languageSelect).fillX().padBottom(10).row();
        final CheckBox blocksCheckbox = new CheckBox("Use Blocks", skin);
        blocksCheckbox.setDisabled(RobotRecharge.blocksEditor == null);
        controlsTable.add(blocksCheckbox).fillX().padBottom(10).row();
        TextButton playButton = new TextButton("New Game", skin);
        controlsTable.add(playButton).fillX().padBottom(10).row();
        final TextButton resumeButton = new TextButton("Resume Game", skin);
        controlsTable.add(resumeButton).fillX();
        stage.addActor(controlsTable);

        // Level select
        final List<LevelInfo> list = new List<>(skin);
        list.setItems(levels);
        ScrollPane levelPane = new ScrollPane(list, skin);
        levelPane.setBounds(200, 50, 600, 800);
        stage.addActor(levelPane);
        list.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stack.show(list.getSelectedIndex());
                resumeButton.setDisabled(!Gdx.files.local("save/" + list.getSelected().getName() + ".json").exists());
            }
        });
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                LevelSave save = new LevelSave(list.getSelected().getName(), blocksCheckbox.isChecked(), "", languageSelect.getSelected());
                RobotRecharge.instance.setScreen(new GameScreen(save));
            }
        });
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                FileHandle save = Gdx.files.internal("save/" + list.getSelected() + ".json");
                RobotRecharge.instance.setScreen(new GameScreen(RobotUtils.json.fromJson(LevelSave.class, save)));
            }
        });
    }
}
