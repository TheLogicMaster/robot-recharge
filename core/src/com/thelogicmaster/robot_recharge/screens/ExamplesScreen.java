package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.Helpers;
import com.thelogicmaster.robot_recharge.IterativeStack;
import com.thelogicmaster.robot_recharge.LevelSave;
import com.thelogicmaster.robot_recharge.RobotRecharge;
import com.thelogicmaster.robot_recharge.code.Example;
import com.thelogicmaster.robot_recharge.code.Language;

public class ExamplesScreen extends MenuScreen {

    public ExamplesScreen(RobotScreen previousScreen) {
        super(previousScreen);

        // Examples list
        Table examplesTable = new Table(skin);
        examplesTable.setBackground("windowTen");
        examplesTable.setBounds(100, 100, 200, 400);
        Array<Example> examples = Helpers.json.fromJson(Array.class, Example.class, Gdx.files.internal("examples.json"));
        final List<Example> list = new List<>(skin);
        list.setItems(examples);
        examplesTable.add(list).grow();
        stage.addActor(examplesTable);

        // Example description and load
        Table descriptionTable = new Table(skin);
        descriptionTable.setBackground("windowTen");
        descriptionTable.setBounds(500, 100, 800, 400);
        final IterativeStack stack = new IterativeStack();
        for (Example example: new Array.ArrayIterator<>(examples))
            stack.add(new Label(example.getDescription(), skin));
        descriptionTable.add(stack).padBottom(20).row();
        final SelectBox<Language> languageSelect = new SelectBox<com.thelogicmaster.robot_recharge.code.Language>(skin);
        Array<Language> languages = new Array<>();
        for (Language language: Language.values())
            if (RobotRecharge.codeEngines.containsKey(language))
                languages.add(language);
        languageSelect.setItems(languages);
        descriptionTable.add(languageSelect).expandX().padBottom(10).row();
        final CheckBox blocksCheckbox = new CheckBox("Use Blocks", skin);
        blocksCheckbox.setDisabled(RobotRecharge.blocksEditor == null);
        descriptionTable.add(blocksCheckbox).expandX().padBottom(10).row();
        TextButton loadButton = new TextButton("Load Example", skin);
        descriptionTable.add(loadButton).expandX();
        stage.addActor(descriptionTable);
        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Example example = list.getSelected();
                String code = blocksCheckbox.isChecked() ? example.getBlocks() : example.getCode().get(languageSelect.getSelected().name());
                LevelSave levelSave = new LevelSave(example.getLevel(), blocksCheckbox.isChecked(), code, languageSelect.getSelected());
                RobotRecharge.instance.setScreen(new GameScreen(levelSave));
            }
        });

        list.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stack.show(list.getSelectedIndex());
            }
        });
    }
}
