package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.ModelInstanceParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.thelogicmaster.robot_recharge.*;
import com.thelogicmaster.robot_recharge.code.Solution;
import com.thelogicmaster.robot_recharge.objectives.Objective;
import com.thelogicmaster.robot_recharge.ui.*;

public class GameScreen extends RobotScreen implements LevelExecutionListener {

    private static final int editorSidebarWidth = 128;

    private Texture hud;
    private final EditorSidebar editorSidebar;
    private final GameControlPanel controlPanel;
    private final PerspectiveCamera cam;
    private final CameraController controller;
    private final Viewport viewport;
    private final LevelSave levelSave;
    private final LevelIntroDialog objectivesDialog;
    private final GameMenu menu;
    private final Environment environment;
    private final ProgressBar loadingBar;
    private final CodeEditor codeEditor;
    private final CommandCatalog catalog;
    private final Level level;
    private final DecalBatch decalBatch;
    private final ModelBatch modelBatch;
    private final LevelIncompleteDialog levelIncompleteDialog;
    private final LevelCompleteDialog levelCompleteDialog;
    private final SolutionsDialog solutionsDialog;

    public GameScreen(final LevelSave levelSave) {
        this.levelSave = levelSave;

        // Todo: load assets from external sources such as zip files
        // Load assets
        assets.load("robot.g3db", Model.class);
        assets.load("hud.png", Texture.class);

        // Setup camera and environment
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0f, -1f, 0f));
        cam = new PerspectiveCamera();
        decalBatch = addDisposable(new DecalBatch(new CameraGroupStrategy(cam)));
        viewport = RobotUtils.createViewport(cam);
        cam.position.x = 15;
        cam.position.y = 30;
        cam.position.z = 20;
        cam.near = 1;
        cam.far = 3000;
        controller = new CameraController(cam);
        controller.target = new Vector3(15, 0, 15);
        cam.lookAt(controller.target);
        cam.update();
        inputMultiplexer.addProcessor(controller);
        controller.setDisabled(true);

        ParticleSystem particleSystem = new ParticleSystem();
        PointSpriteParticleBatch pointSpriteBatch = new PointSpriteParticleBatch();
        pointSpriteBatch.setCamera(viewport.getCamera());
        particleSystem.add(pointSpriteBatch);
        BillboardParticleBatch billboardBatch = new BillboardParticleBatch();
        billboardBatch.setCamera(viewport.getCamera());
        particleSystem.add(billboardBatch);
        particleSystem.add(new ModelInstanceParticleBatch());
        ParticleEffectLoader.ParticleEffectLoadParameter particleParameter =
                new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
        assets.getManager(0).setLoader(ParticleEffect.class, new PreconfiguredParticleEffectLoader(particleParameter));

        // Load level
        level = new Level(RobotAssets.json.fromJson(LevelData.class, Gdx.files.internal("levels/" + levelSave.getLevel() + ".json")),
                this, viewport, particleSystem, RobotRecharge.codeEngines.get(levelSave.getLanguage()), levelSave.isUsingBlocks());
        addDisposable(level);
        level.loadAssets(assets);

        // Create loading bar
        loadingBar = new ProgressBar(-20f, 100f, 1f, false, skin);
        loadingBar.setBounds(uiViewport.getWorldWidth() / 2 - 500, uiViewport.getWorldHeight() / 2 - 200, 1000, 50);

        // Todo: Create display for progressive objectives like Energy and Actions

        // Create settings menu
        menu = new GameMenu(new GameMenu.GameMenuListener() {
            @Override
            public void onExit() {
                dispose();
            }

            @Override
            public void onClose() {
                controller.setDisabled(false);
            }

            @Override
            public void onGridCheckbox(boolean checked) {
                level.showGrid(checked);
            }

            @Override
            public void onObjectives() {
                objectivesDialog.show(stage);
            }

            @Override
            public void onSolutions() {
                solutionsDialog.show(stage);
            }
        });

        // Create main control panel
        controlPanel = new GameControlPanel(skin, new GameControlPanel.ControlPanelListener() {
            @Override
            public void onPlay() {
                level.start();
            }

            @Override
            public void onPause() {
                level.pause();
            }

            @Override
            public void onReset() {
                resetLevel();
            }

            @Override
            public void onProgram() {
                controlPanel.setTouchable(Touchable.disabled);
                controller.setDisabled(true);
                editorSidebar.addAction(Actions.moveTo(0, 0, 0.25f));
                if (!levelSave.isUsingBlocks())
                    codeEditor.setVisible(true);
                else
                    RobotRecharge.blocksEditor.show();
            }

            @Override
            public void onFastForward() {
                level.toggleFastForward();
            }

            @Override
            public void onSettings() {
                menu.show(stage);
                level.pause();
                controller.setDisabled(true);
            }
        });
        controlPanel.setBounds(20, uiViewport.getWorldHeight() - 148, uiViewport.getWorldWidth() - 40, 128);
        stage.addActor(controlPanel);

        // Create editor sidebar
        editorSidebar = new EditorSidebar(skin, new EditorSidebar.EditorSidebarListener() {
            @Override
            public void onClose() {
                controlPanel.setTouchable(Touchable.childrenOnly);
                controller.setDisabled(false);
                editorSidebar.addAction(Actions.moveTo(-editorSidebarWidth, 0, 0.25f));
                if (!levelSave.isUsingBlocks()) {
                    codeEditor.setVisible(false);
                    catalog.setVisible(false);
                } else
                    RobotRecharge.blocksEditor.hide();
            }

            @Override
            public void onRevert() {
                if (!levelSave.isUsingBlocks())
                    codeEditor.setCode(levelSave.getCode());
                else
                    RobotRecharge.blocksEditor.load(levelSave.getCode());
            }

            @Override
            public void onSave() {
                if (!levelSave.isUsingBlocks()) {
                    level.setCode(codeEditor.getCode());
                    levelSave.setCode(codeEditor.getCode());
                    saveLevel();
                    Gdx.app.debug("Saved Code", codeEditor.getCode());
                } else {
                    RobotRecharge.blocksEditor.save(blocks -> {
                        levelSave.setCode(blocks);
                        level.setBlocklyData(blocks);
                        Gdx.app.debug("Saved Blocks", blocks);
                    });
                    RobotRecharge.blocksEditor.generateCode(levelSave.getLanguage(), code -> {
                        level.setCode(code);
                        saveLevel();
                        Gdx.app.debug("Generated Code", code);
                    });
                }
            }
        });
        editorSidebar.setBounds(-editorSidebarWidth, 0, editorSidebarWidth, viewport.getWorldHeight());
        stage.addActor(editorSidebar);

        // Create code editor window
        codeEditor = new CodeEditor(skin, levelSave.getLanguage(), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                catalog.setVisible(!catalog.isVisible());
                catalog.setPosition(viewport.getWorldWidth() - catalog.getWidth() - 50, viewport.getWorldHeight() - catalog.getHeight() - 50);
            }
        });
        codeEditor.setBounds(editorSidebarWidth, 0, uiViewport.getWorldWidth() - editorSidebarWidth, uiViewport.getWorldHeight());
        stage.addActor(codeEditor);

        // Code editor command catalog
        catalog = new CommandCatalog(skin, levelSave.getLanguage());
        stage.addActor(catalog);

        // Create intro dialog
        objectivesDialog = new LevelIntroDialog(levelSave.getLevel(), level.getObjectives(), levelSave.isUsingBlocks(),
                () -> controller.setDisabled(false)
        );

        // Level Fail Dialog
        levelIncompleteDialog = new LevelIncompleteDialog(level.getObjectives(), levelSave.isUsingBlocks());

        // Level Completion Dialog
        levelCompleteDialog = new LevelCompleteDialog(levelSave, this::dispose);

        solutionsDialog = new SolutionsDialog(level.getSolutions(), new SolutionsDialog.SolutionsListener() {
            @Override
            public void onClose() {
                controller.setDisabled(false);
            }

            @Override
            public void onLoad(Solution solution) {
                if (!levelSave.isUsingBlocks()) {
                    String code = solution.getCode().get(levelSave.getLanguage().name());
                    levelSave.setCode(code);
                    saveLevel();
                    level.setCode(code);
                    codeEditor.setCode(code);
                } else {
                    RobotRecharge.blocksEditor.load(solution.getBlocks());
                    level.setBlocklyData(solution.getBlocks());
                    RobotRecharge.blocksEditor.generateCode(levelSave.getLanguage(), level::setCode);
                }
            }
        });
    }

    @Override
    protected void doneLoading() {
        // Get assets
        hud = assets.get("hud.png");
        level.assetsLoaded(assets);

        // Setup level
        if (!levelSave.isUsingBlocks()) {
            level.setCode(levelSave.getCode());
            codeEditor.setCode(levelSave.getCode());
        } else {
            RobotRecharge.assets.updateBlocklyTheme();
            RobotRecharge.blocksEditor.load(levelSave.getCode());
            level.setBlocklyData(levelSave.getCode());
            RobotRecharge.blocksEditor.generateCode(levelSave.getLanguage(), level::setCode);
        }

        resetLevel();

        if (RobotRecharge.ttsEngine != null && RobotRecharge.prefs.getTTSVolume() > 0)
            RobotRecharge.ttsEngine.init();

        objectivesDialog.show(stage);
    }

    @Override
    protected void drawLoading(float delta) {
        spriteBatch.begin();
        RobotRecharge.assets.fontHuge.draw(spriteBatch, "Loading...", uiViewport.getWorldWidth() / 2 - 300,
                uiViewport.getWorldHeight() / 2 + 100);
        loadingBar.setValue(assets.getProgress() * 100);
        loadingBar.act(delta);
        loadingBar.draw(spriteBatch, 1);
        spriteBatch.end();
    }

    @Override
    protected boolean isDoneLoading() {
        return super.isDoneLoading() && isEditorLoaded();
    }

    @Override
    public void draw(float delta) {
        controller.update();
        cam.update();

        // Draw background
        spriteBatch.begin();
        level.drawBackground(spriteBatch, delta);
        spriteBatch.end();

        // Draw level
        modelBatch.begin(cam);
        level.render(modelBatch, decalBatch, environment, delta);
        modelBatch.end();
        decalBatch.flush();

        uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        // Draw HUD background
        spriteBatch.begin();
        spriteBatch.draw(hud, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        spriteBatch.end();

        // Draw UI
        super.draw(delta);
    }

    private void saveLevel() {
        RobotRecharge.prefs.saveLevel(levelSave);
    }

    private void resetLevel() {
        level.reset();
    }

    private boolean isEditorLoaded() {
        if (levelSave.isUsingBlocks())
            return RobotRecharge.blocksEditor.isLoaded();
        return true;
    }

    @Override
    public void onLevelIncomplete(Array<Objective> failed) {
        controlPanel.disablePlay();
        levelIncompleteDialog.show(stage, failed);
    }

    @Override
    public void onLevelFail(String reason) {
        controlPanel.disablePlay();
        // Todo: show dialog with reason
    }

    @Override
    public void onLevelComplete(float completionTime, int length, int calls) {
        controlPanel.disablePlay();
        levelCompleteDialog.show(stage, completionTime, length, calls);
        RobotRecharge.prefs.unlockLevel(RobotUtils.getLevelIndex(levelSave.getLevel()) + 1);
    }

    @Override
    public void onLevelError(String error) {
        Dialogs.showErrorDialog(stage, error).setMovable(false);
        controlPanel.disablePlay();
    }

    @Override
    public void onLevelAbort() {
    }

    @Override
    public void onLevelPause() {
        controlPanel.pause();
    }

    @Override
    public void resize(int width, int height) {

        super.resize(width, height);
        viewport.update(width, height);
        if (RobotRecharge.blocksEditor != null)
            RobotRecharge.blocksEditor.setWidth(Gdx.graphics.getWidth() - (int) uiViewport.project(new Vector2(editorSidebarWidth, 0)).x);
    }
}
