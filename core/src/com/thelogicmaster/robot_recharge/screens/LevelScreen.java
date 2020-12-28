package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.dialog.ConfirmDialogListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.thelogicmaster.robot_recharge.*;
import com.thelogicmaster.robot_recharge.code.Language;
import com.thelogicmaster.robot_recharge.ui.IterativeStack;

public class LevelScreen extends MenuScreen {

    private final TextButton resumeButton;
    private final Array<TextButton> levelButtons;
    private LevelInfo selected;

    public LevelScreen(RobotScreen previousScreen) {
        super(previousScreen);

        final Array<LevelInfo> levels = RobotRecharge.assets.levelInfo;

        // Level description
        final IterativeStack stack = new IterativeStack();
        for (LevelInfo level: levels) {
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
        controlsTable.setBounds(uiViewport.getWorldWidth() - 1000, uiViewport.getWorldHeight() - 590,
                800, 350);
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
        resumeButton = new TextButton("Resume Game", skin);
        controlsTable.add(resumeButton).fillX();
        stage.addActor(controlsTable);

        // Todo: Add completion/locked icons next to buttons using probably tables
        // Level select
        Table levelButtonTable = new Table(skin);
        levelButtonTable.pad(0, 10, 0, 10);
        levelButtons = new Array<>();
        for (final LevelInfo levelInfo: levels) {
            TextButton button = new TextButton(levelInfo.getName(), skin, "levelLabel");
            button.setProgrammaticChangeEvents(false);
            levelButtons.add(button);
            levelButtonTable.add(button).growX().padBottom(10).row();
            if (selected == null) {
                selected = levelInfo;
                button.setChecked(true);
            }
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    RobotUtils.playNavigationSound();
                    for (int i = 0; i < levelButtons.size; i++)
                        levelButtons.get(i).setChecked(levelButtons.get(i).getText().toString().equals(levelInfo.getName()));
                    stack.show(levels.indexOf(levelInfo, true));
                    updateButtons();
                    selected = levelInfo;
                }
            });
        }
        Table levelTable = new Table(skin);
        levelTable.setBackground("windowTen");
        levelTable.pad(10);
        levelTable.setBounds(200, 50, 600, 800);
        ScrollPane levelPane = new ScrollPane(levelButtonTable, skin, "levelPane");
        levelPane.setFadeScrollBars(false);
        levelTable.add(levelPane).grow();
        stage.addActor(levelTable);

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                OptionDialogAdapter adapter = new OptionDialogAdapter() {
                    @Override
                    public void yes() {
                        RobotUtils.playNavigationSound();
                        LevelSave save = new LevelSave(selected.getName(), blocksCheckbox.isChecked(), "",
                                languageSelect.getSelected());
                        RobotRecharge.instance.setScreen(new GameScreen(save));
                        RobotRecharge.assets.titleMusic.stop();
                    }

                    @Override
                    public void cancel() {
                        RobotUtils.playNavigationSound();
                    }
                };
                if (RobotRecharge.prefs.hasSave(selected.getName())) {
                    Dialogs.showOptionDialog(stage, "Overwrite Existing Save?", "" +
                                    "Are you sure you want to create a new game? This will \n" +
                                    "overwrite your existing save for this level.",
                            Dialogs.OptionDialogType.YES_CANCEL, adapter).setMovable(false);
                } else
                    adapter.yes();
            }
        });
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                RobotRecharge.instance.setScreen(new GameScreen(RobotRecharge.prefs.getSave(selected.getName())));
            }
        });
    }

    @Override
    public void show() {
        super.show();
        updateButtons();
    }

    private void updateButtons() {
        resumeButton.setDisabled(!RobotRecharge.prefs.hasSave(selected.getName()));
        for (int i = 0; i < levelButtons.size; i++)
            levelButtons.get(i).setDisabled(i > RobotRecharge.prefs.getUnlockedLevel());
    }
}
