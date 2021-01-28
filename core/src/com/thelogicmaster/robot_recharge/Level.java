package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.OrderedSet;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thelogicmaster.robot_recharge.blocks.Block;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.Solution;
import com.thelogicmaster.robot_recharge.objectives.Objective;
import com.thelogicmaster.robot_recharge.structures.Structure;

public class Level implements Disposable, Renderable3D, AssetConsumer, RobotExecutionListener {

    private final int xSize, ySize, zSize;
    private final float levelHeight;
    private final Position startPosition;
    private final Direction startDirection;
    private final Array<Structure> structures;
    private final Array<Objective> objectives;
    private final String levelModelName, backgroundName;
    private final Array<Solution> solutions;

    private Robot robot;
    private final ParticleSystem particleSystem;
    private final OrderedSet<LevelEventListener> levelListeners = new OrderedSet<>();
    private final OrderedSet<RobotListener> robotListeners = new OrderedSet<>();
    private final Array<LevelEvent> events = new Array<>();
    private final Array<Block> realBlocks = new Array<>();
    private final LevelExecutionListener listener;
    private final boolean useBlocks;
    private final CodeEngine engine;
    private final Viewport viewport;
    private Texture background;
    private Block[][][] blocks;
    private ModelInstance level, grid;
    private Model gridModel;
    private boolean showingGrid;
    private String blocklyData, code;
    private float runTime;
    private boolean setup; // If setup is in progress

    public Level(LevelData levelData, LevelExecutionListener listener, Viewport viewport, ParticleSystem particleSystem, CodeEngine engine, boolean useBlocks) {
        xSize = levelData.getXSize();
        ySize = levelData.getYSize();
        zSize = levelData.getZSize();
        levelHeight = levelData.getLevelHeight();
        levelModelName = levelData.getLevelModel();
        objectives = levelData.getObjectives();
        structures = levelData.getStructures();
        backgroundName = levelData.getBackground();
        solutions = levelData.getSolutions();
        startDirection = levelData.getStartDirection();
        startPosition = levelData.getStartPosition();
        this.listener = listener;
        this.useBlocks = useBlocks;
        this.viewport = viewport;
        this.particleSystem = particleSystem;
        this.engine = engine;
    }

    public Robot getRobot() {
        return robot;
    }

    public Array<Structure> getStructures() {
        return structures;
    }

    public Array<Objective> getObjectives() {
        return objectives;
    }

    public Array<Solution> getSolutions() {
        return solutions;
    }

    public void drawBackground(SpriteBatch batch) {
        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    public void showGrid(boolean shown) {
        this.showingGrid = shown;
    }

    private void createGrid() {
        if (gridModel != null)
            gridModel.dispose();
        // Todo: switch to ModelBuilder.createLineGrid()
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES, VertexAttributes.Usage.Position
                | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.BLACK);
        // Changing blocks aren't compatible with static grid mesh
        /*for (Block block: new Array.ArrayIterator<>(realBlocks)) {
            Position pos = block.getPosition();
            if (blocks[pos.x][pos.y + 1][pos.z] != null)
                continue;
            builder.rect(pos.x, pos.y + 1.01f, pos.z,
                    pos.x + 1, pos.y + 1.01f, pos.z,
                    pos.x + 1, pos.y + 1.01f, pos.z + 1,
                    pos.x, pos.y + 1.01f, pos.z + 1,
                    0, 1, 0);
        }*/
        for (float t = 0; t <= 30; t += 1) {
            builder.line(t, 0.01f, 0, t, 0.01f, 30);
            builder.line(0, 0.01f, t, 30, 0.01f, t);
        }
        gridModel = modelBuilder.end();
        grid = new ModelInstance(gridModel);
    }

    /**
     * Completely resets and regenerates the level
     */
    public void reset() {
        robot.reset(startPosition.cpy(), startDirection);
        realBlocks.clear();
        levelListeners.clear();
        robotListeners.clear();
        blocks = new Block[xSize][ySize][zSize];
        setup = true;
        for (Structure structure : new Array.ArrayIterator<>(structures))
            structure.generate(this);
        for (Block block : new Array.ArrayIterator<>(realBlocks))
            block.setup(this);
        particleSystem.removeAll();
        createGrid();
        events.clear();
        setup = false;
        runTime = 0f;
    }

    public void start() {
        robot.start();
    }

    public void pause() {
        robot.pause();
    }

    public void setCode(String code) {
        this.code = code;
        robot.setCode(code);
    }

    public void setBlocklyData(String data) {
        blocklyData = data;
    }

    public void toggleFastForward() {
        robot.setFastForward(!robot.isFastForward());
    }

    /**
     * Determines if a position is within the level bounds
     *
     * @param position to verify
     * @return validity of position
     */
    public boolean isPositionInvalid(Position position) {
        return position.x < 0 || position.x >= xSize
                || position.y < 0 || position.y >= ySize
                || position.z < 0 || position.z >= zSize;
    }

    public Block getBlock(Position position) {
        if (isPositionInvalid(position))
            return null;
        return blocks[position.x][position.y][position.z];
    }

    public boolean setBlock(Block block, Position position) {
        if (isPositionInvalid(position))
            return false;
        block.setPosition(position);
        blocks[position.x][position.y][position.z] = block;
        realBlocks.add(block);
        if (!setup) {
            // Alert listneres
        }
        return true;
    }

    public void moveBlock(Block block, Position position) {
        Position oldPos = block.getPosition();
        blocks[oldPos.x][oldPos.y][oldPos.z] = null;
        blocks[position.x][position.y][position.z] = block;
        block.setPosition(position);
    }

    public void removeBlock(Position position) {
        if (!isPositionInvalid(position))
            removeBlock(getBlock(position));
    }

    public void removeBlock(Block block) {
        if (!realBlocks.contains(block, true))
            return;
        Position position = block.getPosition();
        blocks[position.x][position.y][position.z] = null;
        realBlocks.removeValue(block, true);
    }

    /**
     * Add listeners in the Structure generate() and Block init() methods
     *
     * @param listener The RobotListener to add
     */
    public void addRobotListener(RobotListener listener) {
        robotListeners.add(listener);
    }

    public void removeRobotListener(RobotListener listener) {
        robotListeners.remove(listener);
    }

    /**
     * Add listeners in the Structure generate() and Block init() methods
     *
     * @param listener The LevelListener to add
     */
    public void addLevelListener(LevelEventListener listener) {
        levelListeners.add(listener);
    }

    public void removeLevelListener(LevelEventListener listener) {
        levelListeners.remove(listener);
    }

    public void emitLevelEvent(LevelEvent event) {
        Gdx.app.debug("LevelEvent", event.toString());
        for (LevelEventListener listener : new OrderedSet.OrderedSetIterator<>(levelListeners))
            listener.onEvent(event);
        events.add(event);
    }

    public void onRobotMove() {
        for (RobotListener listener : new OrderedSet.OrderedSetIterator<>(robotListeners))
            listener.onRobotMove(robot);
    }

    public void onRobotSubMove() {
        for (RobotListener listener : new OrderedSet.OrderedSetIterator<>(robotListeners))
            listener.onRobotSubMove(robot);
    }

    public void onRobotCrash(Position crash) {
        for (RobotListener listener : new OrderedSet.OrderedSetIterator<>(robotListeners))
            listener.onRobotCrash(robot, crash);
    }

    @Override
    public void onExecutionPaused() {
        listener.onLevelPause();
    }

    @Override
    public void onExecutionFinish() {
        completeLevel();
    }

    @Override
    public void onExecutionInterrupted() {
        listener.onLevelAbort();
    }

    @Override
    public void onExecutionError(String error) {
        listener.onLevelError(error);
    }

    public void playParticleEffect(ParticleEffect effect, Vector3 position) {
        if (Gdx.app.getType() == Application.ApplicationType.WebGL)
            return;
        ParticleEffect particles = effect.copy();
        particles.translate(position);
        particles.init();
        particles.start();
        particleSystem.add(particles);
    }

    /**
     * Call when the level has been completed
     */
    public void completeLevel() {
        // Todo: add a separate "Energy" objective for movements
        // Todo: Rename MaxCalls Objective to MaxActions or so
        int length;
        if (useBlocks)
            length = blocklyData.split("robot_").length - 1;
        else
            length = code.split("Robot.").length - 1;
        Array<Objective> failed = new Array<>();
        for (Objective objective : new Array.ArrayIterator<>(objectives))
            if (!objective.check(length, robot.getCalls(), runTime, events))
                failed.add(objective);
        robot.stop();
        if (failed.size == 0)
            listener.onLevelComplete(runTime, length, robot.getCalls());
        else
            listener.onLevelIncomplete(failed);
    }

    /**
     * Call when the level has been failed
     * @param reason The reason for level failure
     */
    public void failLevel(String reason) {
        robot.stop();
        listener.onLevelFail(reason);
    }

    @Override
    public void loadAssets(AssetMultiplexer assetMultiplexer) {
        for (Structure structure : structures)
            structure.loadAssets(assetMultiplexer);
        assetMultiplexer.load("levels/" + levelModelName, Model.class);
        assetMultiplexer.load("robot.g3db", Model.class);
        assetMultiplexer.load("levels/" + backgroundName, Texture.class);
    }

    @Override
    public void assetsLoaded(AssetMultiplexer assetMultiplexer) {
        for (Structure structure : structures)
            structure.assetsLoaded(assetMultiplexer);
        level = new ModelInstance(RobotUtils.cleanModel(assetMultiplexer.get("levels/" + levelModelName)));
        level.transform.setTranslation(xSize / 2f, -levelHeight, zSize / 2f);
        robot = new Robot(new ModelInstance(RobotUtils.cleanModel(assetMultiplexer.get("robot.g3db"))), engine,
                viewport, this);
        background = assetMultiplexer.get("levels/" + backgroundName);
    }

    @Override
    public void render(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment, float delta) {
        if (!robot.isRunning())
            delta = 0;
        else if (robot.isFastForward())
            delta *= 2;
        runTime += delta;
        robot.render(modelBatch, decalBatch, environment, delta);
        for (Block block : realBlocks)
            block.render(modelBatch, decalBatch, environment, delta);
        for (Structure structure : structures)
            structure.render(modelBatch, decalBatch, environment, delta);
        modelBatch.render(level, environment);
        if (delta > 0f)
            particleSystem.update(delta);
        particleSystem.begin();
        particleSystem.draw();
        particleSystem.end();
        modelBatch.render(particleSystem);
        if (showingGrid)
            modelBatch.render(grid, environment);
    }

    @Override
    public void dispose() {
        for (Structure structure : structures)
            structure.dispose();
        gridModel.dispose();
        robot.dispose();
    }

    @Override
    public String toString() {
        return "Level{" +
                "xSize=" + xSize +
                ", ySize=" + ySize +
                ", zSize=" + zSize +
                ", levelHeight=" + levelHeight +
                ", startPosition=" + startPosition +
                ", startDirection=" + startDirection +
                ", structures=" + structures +
                ", objectives=" + objectives +
                ", levelModelName='" + levelModelName + '\'' +
                ", backgroundName='" + backgroundName + '\'' +
                ", solutions=" + solutions +
                ", robot=" + robot +
                ", realBlocks=" + realBlocks +
                ", useBlocks=" + useBlocks +
                ", showingGrid=" + showingGrid +
                ", blocklyData='" + blocklyData + '\'' +
                ", code='" + code + '\'' +
                ", runTime=" + runTime +
                ", setup=" + setup +
                '}';
    }
}
