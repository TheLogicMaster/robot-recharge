package com.thelogicmaster.robot_recharge.structures;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.thelogicmaster.robot_recharge.BlockPlaceholder;
import com.thelogicmaster.robot_recharge.Level;
import com.thelogicmaster.robot_recharge.Position;
import com.thelogicmaster.robot_recharge.blocks.Block;

public class BlocksStructure extends Structure {

    private OrderedMap<String, Block> templates;
    private Array<BlockPlaceholder> blocks;

    public BlocksStructure() {
        this(new Position(), 0, new OrderedMap<String, Block>(), new Array<BlockPlaceholder>());
    }

    public BlocksStructure(Position position, int rotation, OrderedMap<String, Block> templates, Array<BlockPlaceholder> blocks) {
        super(position, rotation);
        this.templates = templates;
        this.blocks = blocks;
    }

    @Override
    public void generate(Level level) {
        super.generate(level);
        for (BlockPlaceholder block : new Array.ArrayIterator<>(blocks))
            level.setBlock(templates.get(block.getBlock()).copy(), transformPosition(block.getPosition().cpy()));
    }

    @Override
    public void loadAssets(AssetManager assetManager) {
        for (ObjectMap.Entry<String, Block> entry : new OrderedMap.OrderedMapEntries<>(templates))
            entry.value.loadAssets(assetManager);
    }

    @Override
    public void assetsLoaded(AssetManager assetManager) {
        for (ObjectMap.Entry<String, Block> entry : new OrderedMap.OrderedMapEntries<>(templates))
            entry.value.assetsLoaded(assetManager);
    }

    @Override
    public void dispose() {
        for (ObjectMap.Entry<String, Block> entry : new OrderedMap.OrderedMapEntries<>(templates))
            entry.value.dispose();
    }

    public OrderedMap<String, Block> getTemplates() {
        return templates;
    }

    public Array<BlockPlaceholder> getBlocks() {
        return blocks;
    }
}
