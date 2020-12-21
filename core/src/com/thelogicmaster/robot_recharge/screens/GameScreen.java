package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.thelogicmaster.robot_recharge.*;
import com.thelogicmaster.robot_recharge.code.Command;
import com.thelogicmaster.robot_recharge.ui.CodeArea;
import com.thelogicmaster.robot_recharge.ui.IterativeStack;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class GameScreen extends RobotScreen implements RobotExecutionListener {

    private static final int editorSidebarWidth = 128;

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
    private final LevelSave levelData;
    private final Dialog settingsMenu;
    private final Environment environment;
    private final ProgressBar loadingBar;
    private final Table codeEditor;
    private final Window catalog;
    private final CodeArea codeArea;
    private final Level level;
    private final DecalBatch decalBatch;

    public GameScreen(final LevelSave levelData) {
        // Todo: remove all 'RobotRecharge.blocksEditor != null' checks from here

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

        // Load level
        level = RobotUtils.json.fromJson(Level.class, Gdx.files.internal("levels/" + levelData.getLevel() + ".json"));
        addDisposable(level);
        level.loadAssets(assetManager);

        // Create loading bar
        loadingBar = new ProgressBar(-20f, 100f, 1f, false, skin);
        loadingBar.setBounds(uiViewport.getWorldWidth() / 2 - 500, uiViewport.getWorldHeight() / 2 - 200, 1000, 50);

        // Create settings menu
        // Todo: Switch to a Window, maybe? Needs input blocking still for rest of screen.
        settingsMenu = new Dialog("Settings", skin);
        settingsMenu.padTop(90);
        settingsMenu.setMovable(false);
        settingsMenu.getContentTable().pad(50, 50, 0, 50);
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.getLabelCell().pad(0, 10, 0, 10);
        TextButton exitButton = new TextButton("Exit to Main Menu", skin);
        exitButton.getLabelCell().pad(0, 10, 0, 10);
        settingsMenu.getContentTable().add(exitButton).row();
        settingsMenu.getContentTable().add(closeButton).padBottom(5);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotRecharge.instance.returnToTitle();
                dispose();
            }
        });
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settingsMenu.hide();
                controller.setDisabled(false);
            }
        });

        // Create main control panel
        controlPanel = new Table();
        controlPanel.setBounds(20, uiViewport.getWorldHeight() - 148, uiViewport.getWorldWidth() - 40, 128);
        programButton = new ImageButton(skin, "programmingGame");
        controlPanel.add(programButton).padRight(10);
        playButton = new ImageButton(skin, "playGame");
        pauseButton = new ImageButton(skin, "pauseGame");
        playPause = new com.thelogicmaster.robot_recharge.ui.IterativeStack(playButton, pauseButton);
        controlPanel.add(playPause).padRight(10);
        ImageButton resetButton = new ImageButton(skin, "resetGame");
        controlPanel.add(resetButton).padRight(uiViewport.getWorldWidth() - 5 * 128 - 20 - 20 - 30);
        ImageButton fastForwardButton = new ImageButton(skin, "fastForwardGame");
        controlPanel.add(fastForwardButton);
        ImageButton settingsButton = new ImageButton(skin, "settingsGame");
        controlPanel.add(settingsButton).padLeft(10);
        controlPanel.align(Align.top);
        stage.addActor(controlPanel);
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
        fastForwardButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                robot.setFastForward(!robot.isFastForward());
            }
        });
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settingsMenu.show(stage);
                robot.pause();
                controller.setDisabled(true);
            }
        });

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
        closeEditorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hideEditor();
            }
        });
        revertEditorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!levelData.usingBlocks())
                    codeArea.setText(levelData.getCode());
                else if (RobotRecharge.blocksEditor != null)
                    RobotRecharge.blocksEditor.load(levelData.getCode());
            }
        });
        saveEditorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!levelData.usingBlocks()) {
                    robot.setCode(codeArea.getText());
                    levelData.setCode(codeArea.getText());
                    saveLevel();
                } else if (RobotRecharge.blocksEditor != null) {
                    RobotRecharge.blocksEditor.save(new Consumer<String>() {
                        @Override
                        public void accept(String blocks) {
                            levelData.setCode(blocks);
                        }
                    });
                    RobotRecharge.blocksEditor.generateCode(levelData.getLanguage(), new Consumer<String>() {
                        @Override
                        public void accept(String code) {
                            robot.setCode(code);
                            saveLevel();
                        }
                    });
                }
            }
        });

        // Todo: Move to dedicated Table subclass
        // Create code editor window
        codeEditor = new Table(skin);
        codeEditor.setBackground("windowTen");
        codeEditor.setBounds(editorSidebarWidth, 0, uiViewport.getWorldWidth() - editorSidebarWidth,
                uiViewport.getWorldHeight());
        final ImageButton catalogButton = new ImageButton(skin, "programmingCatalog");
        codeEditor.add(catalogButton).padLeft(10).padTop(10).padRight(100).left();
        codeEditor.add(new Label(levelData.getLanguage().name() + " Editor", skin, "large")).left().expandX().row();
        final Table codeTable = new Table();
        final TextArea lineNumbers = new TextArea("", skin);
        lineNumbers.setDisabled(true);
        codeTable.add(lineNumbers).width(70).fillY();
        codeArea = new CodeArea(skin);
        codeArea.getStyle().background.setLeftWidth(10);
        codeArea.getStyle().background.setTopHeight(10);
        codeArea.getStyle().background.setRightWidth(10);
        codeArea.getStyle().background.setBottomHeight(10);
        codeArea.setProgrammaticChangeEvents(true);
        codeTable.add(codeArea).grow();
        final ScrollPane codeScrollPane = new ScrollPane(codeTable, skin);
        codeScrollPane.setForceScroll(false, true);
        codeScrollPane.setOverscroll(false, false);
        codeScrollPane.setFadeScrollBars(false);
        codeScrollPane.setFlickScroll(false);
        codeEditor.add(codeScrollPane).pad(10, 10, 10, 10).colspan(2).grow();
        codeEditor.setVisible(false);
        stage.addActor(codeEditor);
        codeArea.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        // Todo: Fix initial line count
                        codeArea.updateLines();
                        //Gdx.app.log("Lines", "" + codeArea.getLines());
                        codeArea.setPrefRows(codeArea.getLines() + 1);
                        codeArea.invalidateHierarchy();
                        if (Math.abs(-codeScrollPane.getHeight() / 2 - codeArea.getCursorY() - codeScrollPane.getScrollY()) > codeScrollPane.getHeight() / 2)
                            codeScrollPane.setScrollY(-codeScrollPane.getHeight() / 2 - codeArea.getCursorY());
                        StringBuilder numbers = new StringBuilder();
                        for (int i = 1; i <= codeArea.getLines(); i++)
                            numbers.append(" ").append(i).append("\n");
                        lineNumbers.setText(numbers.toString());
                    }
                });
            }
        });
        catalogButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                catalog.setVisible(!catalog.isVisible());
                catalog.setPosition(viewport.getWorldWidth() - catalog.getWidth() - 50, viewport.getWorldHeight() - catalog.getHeight() - 50);
            }
        });

        // Code editor command catalog
        catalog = new Window("Command Catalog", skin);
        catalog.padTop(100).padLeft(20);
        catalog.setSize(900, 300);
        ImageButton catalogCloseButton = new ImageButton(skin, "close");
        catalog.getTitleTable().add(catalogCloseButton).padRight(10).size(80, 80).right();
        final List<String> commandList = new List<>(skin);
        final com.thelogicmaster.robot_recharge.ui.IterativeStack commandInfoStack = new IterativeStack();
        final Array<com.thelogicmaster.robot_recharge.code.Command> commands = RobotUtils.json.fromJson(Array.class, Command.class,
                Gdx.files.internal("language/commands-" + levelData.getLanguage().name().toLowerCase() + ".json"));
        Array<String> commandLabels = new Array<>();
        for (com.thelogicmaster.robot_recharge.code.Command command : new Array.ArrayIterable<>(commands)) {
            commandLabels.add(command.getName());
            Table infoTable = new Table(skin);
            infoTable.setBackground("windowTen");
            infoTable.align(Align.top);
            infoTable.pad(10, 20, 10, 20);
            StringBuilder args = new StringBuilder(command.getName()).append("(");
            for (String arg : command.getArgs())
                args.append(arg).append(",");
            if (command.getArgs().length > 0)
                args.deleteCharAt(args.length() - 1);
            args.append(")");
            Label argsLabel = new Label(args.toString(), skin);
            argsLabel.setWrap(true);
            infoTable.add(argsLabel).growX().left().row();
            Label descriptionLabel = new Label(command.getDescription(), skin);
            descriptionLabel.setWrap(true);
            infoTable.add(descriptionLabel).growX().left().row();
            Label exampleLabel = new Label("Ex: " + command.getExample(), skin);
            exampleLabel.setWrap(true);
            infoTable.add(exampleLabel).growX().left();
            commandInfoStack.add(infoTable);
        }
        commandList.setItems(commandLabels);
        ScrollPane commandPane = new ScrollPane(commandList, skin);
        commandPane.setForceScroll(false, true);
        commandPane.setScrollingDisabled(true, false);
        commandPane.setFadeScrollBars(false);
        catalog.add(commandPane).width(300).growY();
        catalog.add(commandInfoStack).grow();
        catalog.setVisible(false);
        catalog.setMovable(true);
        stage.addActor(catalog);
        commandList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                commandInfoStack.show(commandList.getSelectedIndex());
            }
        });
        catalogCloseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                catalog.setVisible(false);
            }
        });
    }

    @Override
    protected void doneLoading() {
        // Get assets
        levelInstance = new ModelInstance(RobotUtils.cleanModel(assetManager.<Model>get("level.g3db")));
        background = assetManager.get("background.jpg");
        hud = assetManager.get("hud.png");
        level.assetsLoaded(assetManager);

        // Setup level
        levelInstance.transform.setTranslation(15, -30, 15);
        robot = addDisposable(new Robot(new ModelInstance(RobotUtils.cleanModel(assetManager.<Model>get("robot.g3db"))),
                this, RobotRecharge.codeEngines.get(levelData.getLanguage()), viewport));
        if (!levelData.usingBlocks()) {
            robot.setCode(levelData.getCode());
            codeArea.setText(levelData.getCode());
        } else if (RobotRecharge.blocksEditor != null) {
            RobotRecharge.blocksEditor.load(levelData.getCode());
            RobotRecharge.blocksEditor.generateCode(levelData.getLanguage(), new Consumer<String>() {
                @Override
                public void accept(String code) {
                    robot.setCode(code);
                }
            });
        }

        level.setRobot(robot);
        robot.setLevel(level);

        resetLevel();

        if (RobotRecharge.ttsEngine != null)
            RobotRecharge.ttsEngine.init();
    }

    @Override
    protected void drawLoading(float delta) {
        spriteBatch.begin();
        RobotRecharge.assets.fontHuge.draw(spriteBatch, "Loading...", uiViewport.getWorldWidth() / 2 - 300,
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
        robot.render(modelBatch, decalBatch, environment, robot.isRunning() ? delta : 0);
        level.render(modelBatch, decalBatch, environment, robot.isRunning() ? delta : 0);
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
        if (Gdx.app.getType() != Application.ApplicationType.WebGL)
            try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(Gdx.files.local("save/" + levelData.getLevel() + ".json").write(false)))) {
                writer.write(RobotUtils.json.prettyPrint(levelData));
            } catch (IOException e) {
                Gdx.app.error("Save Level", "Failed to save level", e);
                Dialogs.showErrorDialog(stage, "Failed to save level", e.getMessage());
            }
    }

    private void resetLevel() {
        level.reset();
    }

    private boolean isEditorLoaded() {
        if (levelData.usingBlocks() && RobotRecharge.blocksEditor != null)
            return RobotRecharge.blocksEditor.isLoaded();
        return true;
    }

    private void showEditor() {
        controlPanel.setTouchable(Touchable.disabled);
        controller.setDisabled(true);
        editorSidebar.addAction(Actions.moveTo(0, 0, 0.25f));
        if (!levelData.usingBlocks())
            codeEditor.setVisible(true);
        else if (RobotRecharge.blocksEditor != null)
            RobotRecharge.blocksEditor.show();
    }

    private void hideEditor() {
        controlPanel.setTouchable(Touchable.childrenOnly);
        controller.setDisabled(false);
        editorSidebar.addAction(Actions.moveTo(-editorSidebarWidth, 0, 0.25f));
        if (!levelData.usingBlocks()) {
            codeEditor.setVisible(false);
            catalog.setVisible(false);
        } else if (RobotRecharge.blocksEditor != null)
            RobotRecharge.blocksEditor.hide();
    }

    private boolean isEditorShown() {
        if (!levelData.usingBlocks())
            return codeEditor.isVisible();
        else
            return RobotRecharge.blocksEditor != null && RobotRecharge.blocksEditor.isShown();
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
        if (RobotRecharge.blocksEditor != null)
            RobotRecharge.blocksEditor.setWidth(
                    Gdx.graphics.getWidth() - (int) uiViewport.project(new Vector2(editorSidebarWidth, 0)).x);
    }
}
