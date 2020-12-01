package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.ray3k.tenpatch.TenPatchDrawable;
import com.thelogicmaster.robot_recharge.*;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.PointLightEx;
import net.mgsx.gltf.scene3d.lights.SpotLightEx;
import net.mgsx.gltf.scene3d.scene.*;
import net.mgsx.gltf.scene3d.shaders.PBRShader;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.utils.EnvironmentUtil;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class GameScreen extends RobotScreen implements RobotListener {

    private final int editorSidebarWidth = 128;

    private final ModelBatch modelBatch;
    private Texture background;
    private Texture hud;
    private final ImageButton pauseButton, playButton, programButton;
    private final Table editorSidebar, controlPanel;
    private ModelInstance levelInstance;
    private final ModelInstance gridInstance;
    private Robot robot;
    private final PerspectiveCamera cam;
    private final CameraController controller;
    private final Viewport viewport;
    private final IterativeStack playPause;
    private final LevelData levelData;
    private VisDialog settingsMenu;
    private final Environment environment;
    private final ProgressBar loadingBar;

    public GameScreen(final LevelData levelData) {
        this.levelData = levelData;

        // Load assets
        assetManager.load("level.g3db", Model.class);
        assetManager.load("robot.g3db", Model.class);
        assetManager.load("background.jpg", Texture.class);
        assetManager.load("hud.png", Texture.class);

        // Create grid model
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES, VertexAttributes.Usage.Position
                | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.BLACK);
        for (float t = 0; t <= 30; t += 1) {
            builder.line(t, 0.01f, 0, t, 0.01f, 30);
            builder.line(0, 0.01f, t, 30, 0.01f, t);
        }
        Model gridModel = modelBuilder.end();
        addDisposable(gridModel);
        gridInstance = new ModelInstance(gridModel);

        // Setup camera and environment
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0f, -1f, 0f));
        cam = new PerspectiveCamera();
        viewport = Helpers.createViewport(cam);
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

        Skin skin = new Skin(Gdx.files.internal("gameSkin.json"));

        // Create laoding bar
        loadingBar = new ProgressBar(0f, 100f, 1f, false, skin, "loadingBar");
        loadingBar.setBounds(uiViewport.getWorldWidth() / 2 - 500, uiViewport.getWorldHeight() / 2 - 200, 1000, 50);

        // Create main control panel
        controlPanel = new Table();
        controlPanel.setBounds(20, uiViewport.getWorldHeight() - 148, uiViewport.getWorldWidth() - 40, 128);
        programButton = new ImageButton(skin, "programming");
        controlPanel.add(programButton).padRight(10);
        playButton = new ImageButton(skin, "play");
        pauseButton = new ImageButton(skin, "pause");
        playPause = new IterativeStack(playButton, pauseButton);
        controlPanel.add(playPause).padRight(10);
        ImageButton resetButton = new ImageButton(skin, "reset");
        controlPanel.add(resetButton).padRight(uiViewport.getWorldWidth() - 5 * 128 - 20 - 20 - 30);
        ImageButton fastforwardButton = new ImageButton(skin, "fastforward");
        controlPanel.add(fastforwardButton);
        ImageButton settingsButton = new ImageButton(skin, "settings");
        controlPanel.add(settingsButton).padLeft(10);
        controlPanel.align(Align.top);
        stage.addActor(controlPanel);

        // Create editor sidebar
        editorSidebar = new Table(skin);
        editorSidebar.setBounds(-editorSidebarWidth, 0, editorSidebarWidth, viewport.getWorldHeight());
        editorSidebar.setBackground("editorSidebar");
        ImageButton closeEditorButton = new ImageButton(skin, "closeEditor");
        editorSidebar.add(closeEditorButton).padBottom(128);
        editorSidebar.row();
        ImageButton saveEditorButton = new ImageButton(skin, "saveEditor");
        editorSidebar.add(saveEditorButton).padBottom(128);
        editorSidebar.row();
        ImageButton revertEditorButton = new ImageButton(skin, "revertEditor");
        editorSidebar.add(revertEditorButton);
        stage.addActor(editorSidebar);

        // Setup UI listeners
        programButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showEditor();
            }
        });
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                robot.pause();
            }
        });
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playPause.next();
                programButton.setDisabled(true);
                robot.start();
            }
        });
        resetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resetLevel();
                playPause.show(0);
                programButton.setDisabled(false);
            }
        });
        fastforwardButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                robot.setFastForward(!robot.isFastForward());
            }
        });
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

            }
        });
        closeEditorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hideEditor();
            }
        });
        revertEditorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (levelData.usingBlocks() && RobotRecharge.blocksEditor != null)
                    RobotRecharge.blocksEditor.load(levelData.getCode());
            }
        });
        saveEditorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (levelData.usingBlocks() && RobotRecharge.blocksEditor != null) {
                    RobotRecharge.blocksEditor.save(new Consumer<String>() {
                        @Override
                        public void accept(String blocks) {
                            if (levelData.usingBlocks())
                                levelData.setCode(blocks);
                        }
                    });
                    RobotRecharge.blocksEditor.generateCode(new Consumer<String>() {
                        @Override
                        public void accept(String code) {
                            robot.setCode(code);
                            if (!levelData.usingBlocks())
                                levelData.setCode(code);
                            saveLevel();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void doneLoading() {
        // Get assets
        levelInstance = new ModelInstance(Helpers.cleanModel(assetManager.<Model>get("level.g3db")));
        background = assetManager.get("background.jpg");
        hud = assetManager.get("hud.png");

        // Setup level
        levelInstance.transform.setTranslation(15, -30, 15);
        robot = new Robot(new ModelInstance(Helpers.cleanModel(assetManager.<Model>get("robot.g3db"))), this);
        if (!levelData.usingBlocks())
            robot.setCode(levelData.getCode());
        else if (RobotRecharge.blocksEditor != null) {
            RobotRecharge.blocksEditor.load(levelData.getCode());
            RobotRecharge.blocksEditor.generateCode(new Consumer<String>() {
                @Override
                public void accept(String code) {
                    robot.setCode(code);
                }
            });
        }
        resetLevel();
    }

    @Override
    protected void drawLoading(float delta) {
        spriteBatch.begin();
        RobotRecharge.fontLarge.draw(spriteBatch, "Loading...", uiViewport.getWorldWidth() / 2 - 300,
                uiViewport.getWorldHeight() / 2 + 100);
        loadingBar.setValue(assetManager.getProgress() * 100);
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
        spriteBatch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        spriteBatch.end();

        // Draw level
        modelBatch.begin(cam);
        modelBatch.render(levelInstance, environment);
        modelBatch.render(gridInstance, environment);
        robot.render(modelBatch, environment, delta);
        modelBatch.end();

        // Draw HUD background
        spriteBatch.begin();
        spriteBatch.draw(hud, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        spriteBatch.end();

        // Draw UI
        super.draw(delta);
    }

    private void saveLevel() {
        try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(Gdx.files.local("save/" + levelData.getLevel() + ".txt").write(false)))) {
            Json json = new Json();
            json.toJson(levelData, writer);
        } catch (IOException e) {
            Gdx.app.error("Save Level", "Failed to save level", e);
            Dialogs.showErrorDialog(stage, "Failed to save level", e.getMessage());
        }
    }

    private void resetLevel() {
        robot.reset(new Vector3(), new Quaternion());
    }

    private boolean isEditorLoaded() {
        if (levelData.usingBlocks() && RobotRecharge.blocksEditor != null)
            return RobotRecharge.blocksEditor.isLoaded();
        else
            return true;
    }

    private void showEditor() {
        controlPanel.setTouchable(Touchable.disabled);
        controller.setDisabled(true);
        editorSidebar.addAction(Actions.moveTo(0, 0, 0.25f));
        if (levelData.usingBlocks() && RobotRecharge.blocksEditor != null)
            RobotRecharge.blocksEditor.show();
    }

    private void hideEditor() {
        controlPanel.setTouchable(Touchable.childrenOnly);
        controller.setDisabled(false);
        editorSidebar.addAction(Actions.moveTo(-editorSidebarWidth, 0, 0.25f));
        if (levelData.usingBlocks() && RobotRecharge.blocksEditor != null)
            RobotRecharge.blocksEditor.hide();
    }

    private boolean isEditorShown() {
        if (levelData.usingBlocks() && RobotRecharge.blocksEditor != null)
            return RobotRecharge.blocksEditor.isShown();
        else
            return false;
    }

    @Override
    public void onExecutionPaused() {
        playPause.show(0);
    }

    @Override
    public void onExecutionFinish() {
        playButton.setDisabled(true);
        pauseButton.setDisabled(true);
        programButton.setDisabled(false);
    }

    @Override
    public void onExecutionInterrupted() {

    }

    @Override
    public void onExecutionError(Exception e) {
        Dialogs.showErrorDialog(stage, e.getMessage()).setMovable(false);
        playButton.setDisabled(true);
        pauseButton.setDisabled(true);
        programButton.setDisabled(false);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
        if (RobotRecharge.blocksEditor != null) {
            int projected = (int) uiViewport.project(new Vector2(editorSidebarWidth, 0)).x;
            RobotRecharge.blocksEditor.resize(projected, Gdx.graphics.getWidth() - projected, Gdx.graphics.getHeight());
        }
    }

    @Override
    public void dispose() {
        robot.stop();
        super.dispose();
    }
}
