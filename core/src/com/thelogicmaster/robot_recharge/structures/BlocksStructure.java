package com.thelogicmaster.robot_recharge.structures;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.thelogicmaster.robot_recharge.*;
import com.thelogicmaster.robot_recharge.blocks.Block;
import lombok.*;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class BlocksStructure extends Structure {

    private OrderedMap<String, Block> templates;
    private Array<BlockPlaceholder> blocks;

    public BlocksStructure(Position position, Direction direction, OrderedMap<String, Block> templates, Array<BlockPlaceholder> blocks) {
        super(position, direction);
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
    public void loadAssets(AssetMultiplexer assetManager) {
        for (ObjectMap.Entry<String, Block> entry : new OrderedMap.OrderedMapEntries<>(templates))
            entry.value.loadAssets(assetManager);
    }

    @Override
    public void assetsLoaded(AssetMultiplexer assetManager) {
        for (ObjectMap.Entry<String, Block> entry : new OrderedMap.OrderedMapEntries<>(templates))
            entry.value.assetsLoaded(assetManager);
    }

    @Override
    public void dispose() {
        for (ObjectMap.Entry<String, Block> entry : new OrderedMap.OrderedMapEntries<>(templates))
            entry.value.dispose();
    }
}
