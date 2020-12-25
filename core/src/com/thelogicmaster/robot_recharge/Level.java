package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
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
import com.thelogicmaster.robot_recharge.blocks.Block;
import com.thelogicmaster.robot_recharge.structures.Structure;

public class Level implements Disposable, Renderable3D, AssetConsumer, RobotListener {

    private final int xSize, ySize, zSize;
    private final float levelHeight;
    private final Array<Structure> structures;
    private final Array<LevelObjective> objectives;
    private final String levelModelName;
    private Robot robot;
    private ParticleSystem particleSystem;
    private final OrderedSet<LevelListener> levelListeners = new OrderedSet<>();
    private final OrderedSet<RobotListener> robotListeners = new OrderedSet<>();
    private final Array<Block> realBlocks = new Array<>();
    private Block[][][] blocks;
    private ModelInstance level, grid;
    private Model gridModel;
    private boolean showingGrid;
    private boolean setup; // If setup is in progress

    public Level(LevelData levelData) {
        this.xSize = levelData.getXSize();
        this.ySize = levelData.getYSize();
        this.zSize = levelData.getZSize();
        this.levelHeight = levelData.getLevelHeight();
        this.levelModelName = levelData.getLevelModelName();
        this.objectives = levelData.getObjectives();
        this.structures = levelData.getStructures();
    }

    public void setup(Robot robot, ParticleSystem particleSystem) {
        this.robot = robot;
        this.particleSystem = particleSystem;
    }

    public Robot getRobot() {
        return robot;
    }

    public Array<Structure> getStructures() {
        return structures;
    }

    public void showGrid(boolean shown) {
        this.showingGrid = shown;
    }

    private void createGrid() {
        if (gridModel != null)
            gridModel.dispose();
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

    public void reset() {
        robot.reset(new Position(), Direction.NORTH);
        realBlocks.clear();
        levelListeners.clear();
        robotListeners.clear();
        blocks = new Block[xSize][ySize][zSize];
        setup = true;
        for (Structure structure : new Array.ArrayIterator<>(structures))
            structure.generate(this);
        for (Block block: new Array.ArrayIterator<>(realBlocks))
            block.setup(this);
        createGrid();
        setup = false;
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
    public void addLevelListener(LevelListener listener) {
        levelListeners.add(listener);
    }

    public void removeLevelListener(LevelListener listener) {
        levelListeners.remove(listener);
    }

    public void emitLevelEvent(LevelEvent event) {
        for (LevelListener listener : new OrderedSet.OrderedSetIterator<>(levelListeners))
            listener.onEvent(event);
    }

    @Override
    public void onRobotMove(Robot robot) {
        for (RobotListener listener : new OrderedSet.OrderedSetIterator<>(robotListeners))
            listener.onRobotMove(robot);
    }

    @Override
    public void onRobotSubMove(Robot robot) {
        for (RobotListener listener : new OrderedSet.OrderedSetIterator<>(robotListeners))
            listener.onRobotSubMove(robot);
    }

    @Override
    public void onRobotCrash(Robot robot, Position crash) {
        for (RobotListener listener : new OrderedSet.OrderedSetIterator<>(robotListeners))
            listener.onRobotCrash(robot, crash);
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

    @Override
    public void loadAssets(AssetManager assetManager) {
        for (Structure structure : new Array.ArrayIterator<>(structures))
            structure.loadAssets(assetManager);
        assetManager.load("levels/" + levelModelName, Model.class);
    }

    @Override
    public void assetsLoaded(AssetManager assetManager) {
        for (Structure structure : new Array.ArrayIterator<>(structures))
            structure.assetsLoaded(assetManager);
        level = new ModelInstance(RobotUtils.cleanModel(assetManager.<Model>get("levels/" + levelModelName)));
        level.transform.setTranslation(xSize / 2f, -levelHeight, zSize / 2f);
    }

    @Override
    public void render(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment, float delta) {
        if (!robot.isRunning())
            delta = 0;
        for (Block block : new Array.ArrayIterator<>(realBlocks))
            block.render(modelBatch, decalBatch, environment, delta);
        for (Structure structure : new Array.ArrayIterator<>(structures))
            structure.render(modelBatch, decalBatch, environment, delta);
        modelBatch.render(level, environment);
        if (showingGrid)
            modelBatch.render(grid, environment);
    }

    @Override
    public void dispose() {
        for (Structure structure : new Array.ArrayIterator<>(structures))
            structure.dispose();
        gridModel.dispose();
    }

    @Override
    public String toString() {
        return "Level{" +
                "xSize=" + xSize +
                ", ySize=" + ySize +
                ", zSize=" + zSize +
                ", structures=" + structures +
                '}';
    }
}
