package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.thelogicmaster.robot_recharge.*;

public class GameScreen extends RobotScreen implements RobotListener {

    private final int editorSidebarWidth = 128;

    private ModelBatch modelBatch;
    private Texture background;
    private Texture hud;
    private Model robotModel;
    private Model levelModel;
    private ImageButton pauseButton, playButton, programButton;
    private Table editorSidebar, controlPanel;
    private ModelInstance level, grid;
    private Robot robot;
    private PerspectiveCamera cam;
    private Environment environment;
    private CameraController controller;
    private Viewport viewport;
    private IterativeStack playPause;
    private boolean useBlocks;
    private String blocks;

    @Override
    public void show() {
        super.show();

        modelBatch = new ModelBatch();
        assetManager.load("level.g3db", Model.class);
        assetManager.load("robot.g3db", Model.class);
        assetManager.load("background.jpg", Texture.class);
        assetManager.load("hud.png", Texture.class);
        assetManager.load("editorSidebar.png", Texture.class);

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.BLACK);
        for (float t = 0; t <= 30; t += 1) {
            builder.line(t, 0.01f, 0, t, 0.01f, 30);
            builder.line(0, 0.01f, t, 30, 0.01f, t);
        }
        Model gridModel = modelBuilder.end();
        addDisposable(gridModel);
        grid = new ModelInstance(gridModel);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
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

        useBlocks = true;

        blocks = "<xml xmlns=\"https://developers.google.com/blockly/xml\"><block type=\"controls_whileUntil\" id=\")a]s" +
                "GO7-T5IP%$HIvB2L\" x=\"134\" y=\"84\"><field name=\"MODE\">UNTIL</field><statement name=\"DO\"><block " +
                "type=\"robot_move\" id=\"S75yeoZ`/e5Rv%F}%SX1\"><value name=\"distance\"><block type=\"math_number\" id" +
                "=\"-r+,j4?5V]f0%z.QD^p{\"><field name=\"NUM\">1</field></block></value><next><block type=\"robot_sleep" +
                "\" id=\"Gj4[f%K@Z2!%wgnk2M`X\"><value name=\"duration\"><block type=\"math_number\" id=\"TwlZ]Yvqp=If;5" +
                "BO{bPE\"><field name=\"NUM\">1</field></block></value><next><block type=\"robot_turn\" id=\"K)AbVjj1gr)" +
                "OR6r*q[th\"><value name=\"distance\"><block type=\"math_number\" id=\"jo)d*8|gyjwC0p:baO#5\"><field nam" +
                "e=\"NUM\">1</field></block></value></block></next></block></next></block></statement></block></xml>";
    }

    @Override
    protected void doneLoading() {
        levelModel = assetManager.get("level.g3db");
        robotModel = assetManager.get("robot.g3db");
        background = assetManager.get("background.jpg");
        hud = assetManager.get("hud.png");

        Helpers.cleanModel(levelModel);
        Helpers.cleanModel(robotModel);
        level = new ModelInstance(levelModel);
        level.transform.set(new Vector3(15, -30, 15), new Quaternion(), new Vector3(0.01f, 0.01f, 0.01f));
        ModelInstance modelInstance = new ModelInstance(robotModel);
        robot = new Robot(modelInstance, this);

        controlPanel = new Table();
        controlPanel.setBounds(20, viewport.getWorldHeight() - 148, viewport.getWorldWidth() - 40, 128);
        Skin skin = new Skin(Gdx.files.internal("gameSkin.json"));
        programButton = new ImageButton(skin, "programming");
        controlPanel.add(programButton).padRight(10);
        playButton = new ImageButton(skin, "play");
        pauseButton = new ImageButton(skin, "pause");
        playPause = new IterativeStack(playButton, pauseButton);
        controlPanel.add(playPause).padRight(10);
        ImageButton resetButton = new ImageButton(skin, "reset");
        controlPanel.add(resetButton).padRight(viewport.getWorldWidth() - 5 * 128 - 20 - 20 - 30);
        ImageButton fastforwardButton = new ImageButton(skin, "fastforward");
        controlPanel.add(fastforwardButton);
        ImageButton settingsButton = new ImageButton(skin, "settings");
        controlPanel.add(settingsButton).padLeft(10);
        controlPanel.align(Align.top);
        //table.debug(Table.Debug.all);
        stage.addActor(controlPanel);

        editorSidebar = new Table();
        editorSidebar.setBounds(-editorSidebarWidth, 0, editorSidebarWidth, viewport.getWorldHeight());
        Texture editorSidebarTexture = assetManager.get("editorSidebar.png");
        addDisposable(editorSidebarTexture);
        editorSidebar.setBackground(new TextureRegionDrawable(editorSidebarTexture));
        ImageButton closeEditorButton = new ImageButton(skin, "close_editor");
        editorSidebar.add(closeEditorButton).padBottom(128);
        editorSidebar.row();
        ImageButton saveEditorButton = new ImageButton(skin, "save_editor");
        editorSidebar.add(saveEditorButton).padBottom(128);
        editorSidebar.row();
        ImageButton revertEditorButton = new ImageButton(skin, "revert_editor");
        editorSidebar.add(revertEditorButton);
        stage.addActor(editorSidebar);

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
                if (useBlocks && RobotGame.blocksEditor != null)
                    RobotGame.blocksEditor.load(blocks);
            }
        });
        saveEditorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (useBlocks && RobotGame.blocksEditor != null) {
                    RobotGame.blocksEditor.save(new Consumer<String>() {
                        @Override
                        public void accept(String blocks) {
                            GameScreen.this.blocks = blocks;
                        }
                    });
                    RobotGame.blocksEditor.generateCode(new Consumer<String>() {
                        @Override
                        public void accept(String code) {
                            robot.setCode(code);
                        }
                    });
                }
            }
        });

        if (useBlocks && RobotGame.blocksEditor != null) {
            RobotGame.blocksEditor.load(blocks);
            RobotGame.blocksEditor.generateCode(new Consumer<String>() {
                @Override
                public void accept(String code) {
                    robot.setCode(code);
                }
            });
        }

        robot.setCode("Robot.move(3);\nRobot.turn(1);\nRobot.sleep(1);\n"); //"while(true) {\nRobot.move(3);\nRobot.turn(1);\nRobot.sleep(1);\n}"

        resetLevel();
    }

    @Override
    public void draw(float delta) {
        controller.update();
        cam.update();
        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        spriteBatch.end();
        modelBatch.begin(cam);
        modelBatch.render(level, environment);
        modelBatch.render(grid);
        robot.render(modelBatch, environment, delta);
        modelBatch.end();
        spriteBatch.begin();
        spriteBatch.draw(hud, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        spriteBatch.end();
    }

    @Override
    protected boolean isDoneLoading() {
        return super.isDoneLoading() && isEditorLoaded();
    }

    private void resetLevel() {
        robot.reset(new Vector3(), new Quaternion());
    }

    private boolean isEditorLoaded() {
        if (useBlocks && RobotGame.blocksEditor != null)
            return RobotGame.blocksEditor.isLoaded();
        else
            return true;
    }

    private void showEditor() {
        controlPanel.setTouchable(Touchable.disabled);
        controller.setDisabled(true);
        editorSidebar.addAction(Actions.moveTo(0, 0, 0.25f));
        if (useBlocks && RobotGame.blocksEditor != null)
            RobotGame.blocksEditor.show();
    }

    private void hideEditor() {
        controlPanel.setTouchable(Touchable.childrenOnly);
        controller.setDisabled(false);
        editorSidebar.addAction(Actions.moveTo(-editorSidebarWidth, 0, 0.25f));
        if (useBlocks && RobotGame.blocksEditor != null)
            RobotGame.blocksEditor.hide();
    }

    private boolean isEditorShown() {
        if (useBlocks && RobotGame.blocksEditor != null)
            return RobotGame.blocksEditor.isShown();
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
        if (RobotGame.blocksEditor != null) {
            int projected = (int) uiViewport.project(new Vector2(editorSidebarWidth, 0)).x;
            RobotGame.blocksEditor.resize(projected, Gdx.graphics.getWidth() - projected, Gdx.graphics.getHeight());
        }
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        robotModel.dispose();
        levelModel.dispose();
        super.dispose();
    }
}
