package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.thelogicmaster.robot_recharge.blocks.Block;
import com.thelogicmaster.robot_recharge.structures.Structure;

public class Level implements Disposable, ModelRenderable, AssetConsumer {
    private int xSize, ySize, zSize;
    private final Array<Structure> structures = new Array<>();

    private transient Block[][][] blocks;
    private transient final Array<NeighborListener> neighborListeners = new Array<>();
    private transient final Array<RobotMovementListener> robotMovementListeners = new Array<>();
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
        neighborListeners.clear();
        robotMovementListeners.clear();
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
        if (block instanceof NeighborListener)
            neighborListeners.add((NeighborListener) block);
        if (block instanceof RobotMovementListener)
            robotMovementListeners.add((RobotMovementListener)block);
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

    public void removeBlock(Block block) {
        Position position = block.getPosition();
        blocks[position.x][position.y][position.z] = null;
        realBlocks.removeValue(block, true);
        if (block instanceof NeighborListener)
            neighborListeners.removeValue((NeighborListener) block, true);
    }

    /**
     * Should be called by structures that need robot events in the generate function call
     * @param listener to receive events
     */
    public void addRobotMovementListener(RobotMovementListener listener){
        robotMovementListeners.add(listener);
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
    public void render(ModelBatch batch, Environment environment, float delta) {
        for (Block block : new Array.ArrayIterator<>(realBlocks))
            block.render(batch, environment, delta);
        for (Structure structure : new Array.ArrayIterator<>(structures))
            structure.render(batch, environment, delta);
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
