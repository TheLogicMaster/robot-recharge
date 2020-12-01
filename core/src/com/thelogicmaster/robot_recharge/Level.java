package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.OrderedSet;
import com.thelogicmaster.robot_recharge.blocks.Block;
import com.thelogicmaster.robot_recharge.structures.IStructure;

public class Level implements Disposable, IModelRenderable {
    private int xSize, ySize, zSize;
    private final Array<IStructure> structures = new Array<>();

    private transient Block[][][] blocks;
    private transient final Array<INeighborListener> neighborListeners = new Array<>();
    private transient final Array<Block> realBlocks = new Array<>();
    private transient boolean setup; // If setup is in progress

    public Level(int xSize, int ySize, int zSize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
    }

    public Level() {

    }

    public void addStructure(IStructure structure) {
        structures.add(structure);
    }

    public void removeStructure(IStructure structure) {
        structures.removeValue(structure, true);
    }

    public void reset() {
        realBlocks.clear();
        neighborListeners.clear();
        blocks = new Block[xSize][ySize][zSize];
        setup = true;
        for (IStructure structure : new Array.ArrayIterator<>(structures))
            structure.generate(this);
        setup = false;
    }

    public Block getBlock(Position position) {
        if (position.x < 0 || position.x >= xSize || position.y < 0 || position.y >= ySize || position.z < 0 || position.z >= zSize)
            return null;
        return blocks[position.x][position.y][position.z];
    }

    public boolean setBlock(Block block, Position position) {
        if (position.x < 0 || position.x >= xSize || position.y < 0 || position.y >= ySize || position.z < 0 || position.z >= zSize)
            return false;
        block.setPosition(position);
        realBlocks.add(block);
        if (block instanceof INeighborListener)
            neighborListeners.add((INeighborListener) block);
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
        if (block instanceof INeighborListener)
            neighborListeners.removeValue((INeighborListener)block, true);
    }

    @Override
    public void render(ModelBatch batch, Environment environment, float delta) {
        for (Block block: new Array.ArrayIterator<>(realBlocks))
            block.render(batch, environment, delta);
        for (IStructure structure : new Array.ArrayIterator<>(structures))
            structure.render(batch, environment, delta);
    }

    @Override
    public void dispose() {
        for (Block block: new Array.ArrayIterator<>(realBlocks))
            block.dispose();
        for (IStructure structure : new Array.ArrayIterator<>(structures))
            structure.dispose();
    }
}
