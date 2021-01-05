package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.thelogicmaster.robot_recharge.GameServices;
import com.thelogicmaster.robot_recharge.RobotRecharge;
import com.thelogicmaster.robot_recharge.RobotUtils;

public class GameMenu extends RobotDialog {

    private boolean unlockedSolution;
    private final GameMenuListener listener;

    public GameMenu(Skin skin, final GameMenuListener listener) {
        super("Menu", skin);
        this.listener = listener;

        final CheckBox gridCheckbox = new CheckBox("Show Grid", skin);
        add(gridCheckbox).padBottom(20).fillX().row();

        TextButton objectivesButton = new PaddedTextButton("View Objectives", skin);
        add(objectivesButton).padBottom(20).fillX().row();

        TextButton solutionsButton = new PaddedTextButton("View Solutions", skin);
        add(solutionsButton).padBottom(20).fillX().row();

        TextButton exitButton = new PaddedTextButton("Exit to Main Menu", skin);
        add(exitButton).padBottom(30).fillX().row();

        TextButton closeButton = new PaddedTextButton("Close", skin);
        add(closeButton).expandY().bottom();

        gridCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                listener.onGridCheckbox(gridCheckbox.isChecked());
            }
        });
        objectivesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                setVisible(false);
                listener.onObjectives();
            }
        });
        solutionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Gdx.app.getType() == Application.ApplicationType.Android && !unlockedSolution && !RobotRecharge.prefs.hasUnlockedSolutions())
                    Dialogs.showOptionDialog(getStage(), "View Solutions", "Watch an ad to view the level solutions?",
                            Dialogs.OptionDialogType.YES_CANCEL, new OptionDialogAdapter() {
                                @Override
                                public void yes() {
                                    RobotUtils.playNavigationSound();
                                    RobotRecharge.gameServices.showRewardAd(new GameServices.RewardAdListener() {
                                        @Override
                                        public void onAdReward() {
                                            showSolution();
                                            unlockedSolution = true;
                                        }
                                    });
                                }

                                @Override
                                public void cancel() {
                                    RobotUtils.playNavigationSound();
                                }
                            });
                else
                    showSolution();
                RobotUtils.playNavigationSound();

            }
        });
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                RobotRecharge.instance.returnToTitle();
                listener.onExit();
            }
        });
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                setVisible(false);
                listener.onClose();
            }
        });

        gridCheckbox.setChecked(true);
    }

    private void showSolution() {
        setVisible(false);
        listener.onSolutions();
    }

    public interface GameMenuListener {

        void onExit();

        void onClose();

        void onGridCheckbox(boolean checked);

        void onObjectives();

        void onSolutions();
    }
}
