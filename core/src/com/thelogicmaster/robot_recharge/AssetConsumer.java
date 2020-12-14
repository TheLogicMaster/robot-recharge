package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.assets.AssetManager;

/**
 * An interface for an entity that requires asset manager assets
 */
public interface AssetConsumer {

    /**
     * Called to collect assets to load
     * @param assetManager AssetManager to load assets
     */
    void loadAssets(AssetManager assetManager);

    /**
     * Called when assets are loaded
     * @param assetManager AssetManager with assets
     */
    void assetsLoaded(AssetManager assetManager);
}
