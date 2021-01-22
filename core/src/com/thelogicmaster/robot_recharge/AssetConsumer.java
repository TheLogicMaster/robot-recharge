package com.thelogicmaster.robot_recharge;

/**
 * An interface for an entity that requires asset manager assets
 */
public interface AssetConsumer {

    /**
     * Called to collect assets to load
     *
     * @param assetManager AssetManager to load assets
     */
    void loadAssets(AssetMultiplexer assetManager);

    /**
     * Called when assets are loaded
     *
     * @param assetManager AssetManager with assets
     */
    void assetsLoaded(AssetMultiplexer assetManager);
}
