package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.GameServices;
import com.thelogicmaster.robot_recharge.RobotRecharge;
import com.thelogicmaster.robot_recharge.RobotUtils;
import com.thelogicmaster.robot_recharge.WindowMode;
import com.thelogicmaster.robot_recharge.ui.PaddedTextButton;

public class SettingsScreen extends MenuScreen {

    public SettingsScreen(RobotScreen previousScreen) {
        super(previousScreen);
        setBackground(new Texture("settingsScreen.png"));

        Table settingsTable = new Table(skin);
        settingsTable.columnDefaults(0).padRight(40).left();
        settingsTable.columnDefaults(1).growX().fillY();
        settingsTable.setBackground("windowTen");
        settingsTable.setBounds(uiViewport.getWorldWidth() / 2 - 700, uiViewport.getWorldHeight() / 2 - 400, 1400, 800);
        settingsTable.pad(30);
        settingsTable.add(new Label("Settings", skin, "large")).padBottom(30).growX().expandY().top().colspan(3).row();

        settingsTable.add(new Label("Music Volume", skin)).padBottom(10);
        final Slider musicSlider = new Slider(0, 100, 1, false, skin);
        musicSlider.setVisualPercent(RobotRecharge.prefs.getMusicVolume());
        final Label musicPercent = new Label(((int) musicSlider.getValue()) + "%", skin, "small");
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotRecharge.prefs.setMusicVolume(musicSlider.getVisualPercent());
                musicPercent.setText((int) musicSlider.getValue() + "%");
                RobotRecharge.assets.titleMusic.setVolume(musicSlider.getVisualPercent());
            }
        });
        settingsTable.add(musicSlider).padBottom(10).padRight(20).left();
        settingsTable.add(musicPercent).padBottom(10).minWidth(50).row();

        settingsTable.add(new Label("SFX Volume", skin)).padBottom(10);
        final Slider effectsSlider = new Slider(0, 100, 1, false, skin);
        effectsSlider.setVisualPercent(RobotRecharge.prefs.getEffectsVolume());
        final Label effectsPercent = new Label(((int) effectsSlider.getValue()) + "%", skin, "small");
        effectsSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotRecharge.prefs.setEffectsVolume(effectsSlider.getVisualPercent());
                effectsPercent.setText((int) effectsSlider.getValue() + "%");
            }
        });
        settingsTable.add(effectsSlider).padBottom(10).padRight(20).left();
        settingsTable.add(effectsPercent).padBottom(10).minWidth(50).row();

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            final TextButton buySolutionsButton = new PaddedTextButton("Unlock All Level Solutions", skin);
            buySolutionsButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    RobotRecharge.gameServices.inAppPurchase("solutions", new GameServices.InAppPurchaseListener() {
                        @Override
                        public void onPurchase() {
                            RobotRecharge.prefs.unlockSolutions();
                            buySolutionsButton.setDisabled(true);
                        }
                    });
                }
            });
            buySolutionsButton.setDisabled(RobotRecharge.prefs.hasUnlockedSolutions());
            settingsTable.add(buySolutionsButton).colspan(3).padBottom(10).row();

            TextButton restoreButton = new PaddedTextButton("Restore Purchases", skin);
            restoreButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    RobotRecharge.gameServices.restorePurchases(new GameServices.RestorePurchasesListener() {
                        @Override
                        public void onRestorePurchases(Array<String> purchases) {
                            RobotRecharge.prefs.restorePurchases(purchases);
                        }
                    });
                }
            });
            settingsTable.add(restoreButton).colspan(3).padBottom(10).row();
        }

        if (RobotRecharge.platformUtils.getWindowModes().size > 0) {
            settingsTable.add(new Label("Window Mode", skin)).padBottom(300);
            final SelectBox<WindowMode> windowModeSelect = new SelectBox<>(skin);
            windowModeSelect.setItems(RobotRecharge.platformUtils.getWindowModes());
            if (Gdx.app.getType() != Application.ApplicationType.WebGL) {
                RobotRecharge.platformUtils.setWindowMode(windowModeSelect.getSelected());
                windowModeSelect.setSelected(RobotRecharge.prefs.getWindowMode());
            }
            windowModeSelect.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    RobotUtils.playNavigationSound();
                    RobotRecharge.platformUtils.setWindowMode(windowModeSelect.getSelected());
                    RobotRecharge.prefs.setWindowMode(windowModeSelect.getSelected());
                }
            });
            settingsTable.add(windowModeSelect).growX().colspan(2).row();
        }

        settingsTable.add().padBottom(300).row();

        stage.addActor(settingsTable);
    }
}
