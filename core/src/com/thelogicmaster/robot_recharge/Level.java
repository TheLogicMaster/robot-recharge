package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.OrderedSet;
import com.thelogicmaster.robot_recharge.blocks.Block;
import com.thelogicmaster.robot_recharge.structures.Structure;

public class Level implements Disposable, Renderable3D, AssetConsumer, RobotListener {
    private int xSize, ySize, zSize;
    private Array<Structure> structures = new Array<>();

    private transient Block[][][] blocks;
    private transient final OrderedSet<LevelListener> levelListeners = new OrderedSet<>();
    private transient final OrderedSet<RobotListener> robotListeners = new OrderedSet<>();
    private transient final Array<Block> realBlocks = new Array<>();
    private transient Robot robot;
    private transient boolean setup; // If setup is in progress

    public Level(int xSize, int ySize, int zSize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
    }

    public Level() {

    }

    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    public Robot getRobot() {
        return robot;
    }

    public void addStructure(Structure structure) {
        structures.add(structure);
    }

    public void removeStructure(Structure structure) {
        structures.removeValue(structure, true);
    }

    public Array<Structure> getStructures() {
        return structures;
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

    @Override
    public void loadAssets(AssetManager assetManager) {
        for (Structure structure : new Array.ArrayIterator<>(structures))
            structure.loadAssets(assetManager);
    }

    @Override
    public void assetsLoaded(AssetManager assetManager) {
        for (Structure structure : new Array.ArrayIterator<>(structures))
            structure.assetsLoaded(assetManager);
    }

    @Override
    public void render(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment, float delta) {
        for (Block block : new Array.ArrayIterator<>(realBlocks))
            block.render(modelBatch, decalBatch, environment, delta);
        for (Structure structure : new Array.ArrayIterator<>(structures))
            structure.render(modelBatch, decalBatch, environment, delta);
    }

    @Override
    public void dispose() {
        for (Structure structure : new Array.ArrayIterator<>(structures))
            structure.dispose();
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
