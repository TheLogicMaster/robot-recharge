package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.thelogicmaster.robot_recharge.LevelInfo;
import com.thelogicmaster.robot_recharge.LevelSave;
import com.thelogicmaster.robot_recharge.RobotRecharge;
import com.thelogicmaster.robot_recharge.RobotUtils;
import com.thelogicmaster.robot_recharge.code.Language;
import com.thelogicmaster.robot_recharge.ui.IterativeStack;
import com.thelogicmaster.robot_recharge.ui.LanguageSelect;
import com.thelogicmaster.robot_recharge.ui.PaddedTextButton;

public class LevelScreen extends MenuScreen {

    private final TextButton resumeButton;
    private final Array<TextButton> levelButtons;
    private LevelInfo selected;

    public LevelScreen(RobotScreen previousScreen) {
        super(previousScreen);
        setBackground(new Texture("levelScreen.png"));

        final Array<LevelInfo> levels = RobotRecharge.assets.levelInfo;

        // Level description
        final IterativeStack stack = new IterativeStack();
        for (LevelInfo level : levels) {
            Table table = new Table(skin);
            table.setBackground("buttonTen");
            Label levelInfo = new Label(level.getDescription(), skin);
            levelInfo.setWrap(true);
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
        final SelectBox<Language> languageSelect = new LanguageSelect(skin);
        controlsTable.add(languageSelect).fillX().padBottom(10).row();
        final CheckBox blocksCheckbox = new CheckBox("Use Blocks", skin);
        blocksCheckbox.setDisabled(RobotRecharge.blocksEditor == null);
        controlsTable.add(blocksCheckbox).fillX().padBottom(10).row();
        TextButton playButton = new TextButton("New Game", skin);
        controlsTable.add(playButton).fillX().padBottom(10).row();
        resumeButton = new PaddedTextButton("Resume Game", skin);
        controlsTable.add(resumeButton).fillX();
        stage.addActor(controlsTable);

        // Todo: Add completion/locked icons next to buttons using probably tables
        // Level select
        Table levelButtonTable = new Table(skin);
        levelButtonTable.pad(0, 10, 0, 10);
        levelButtons = new Array<>();
        for (final LevelInfo levelInfo : levels) {
            TextButton button = new PaddedTextButton(levelInfo.getName(), skin, "levelLabel");
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
                    selected = levelInfo;
                    updateButtons();
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
                if (RobotRecharge.prefs.hasLevelSave(selected.getName())) {
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
                LevelSave save = RobotRecharge.prefs.getLevelSave(selected.getName());
                if (RobotRecharge.blocksEditor == null && save.usingBlocks()) {
                    Dialogs.showErrorDialog(stage, "This save was created using Blockly, which isn't available");
                    return;
                }
                RobotRecharge.instance.setScreen(new GameScreen(save));
            }
        });
    }

    @Override
    public void show() {
        super.show();
        updateButtons();
    }

    private void updateButtons() {
        resumeButton.setDisabled(!RobotRecharge.prefs.hasLevelSave(selected.getName()));
        for (int i = 0; i < levelButtons.size; i++)
            levelButtons.get(i).setDisabled(i > RobotRecharge.prefs.getUnlockedLevel());
    }
}
