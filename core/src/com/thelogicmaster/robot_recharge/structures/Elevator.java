package com.thelogicmaster.robot_recharge.structures;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.Level;
import com.thelogicmaster.robot_recharge.Position;
import com.thelogicmaster.robot_recharge.blocks.Block;
import com.thelogicmaster.robot_recharge.blocks.ElevatorBlock;

public class Elevator implements IStructure {

    private Position position;
    private int height;

    public Elevator() {
    }

    public Elevator(Position position, int height) {
        this.position = position;
        this.height = height;
    }

    @Override
    public void generate(Level level) {
        for (int i = 0; i < height; i++)
            level.setBlock(new ElevatorBlock(this), new Position(position.x, position.y + i, position.z));
    }

    public void render(ModelBatch batch, Environment environment, float delta) {

    }

    @Override
    public void loadAssets(AssetManager assetManager) {

    }

    @Override
    public void assetsLoaded(AssetManager assetManager) {

    }

    @Override
    public void dispose() {

    }
}
