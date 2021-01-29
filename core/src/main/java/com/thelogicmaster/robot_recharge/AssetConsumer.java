package com.thelogicmaster.robot_recharge;

/**
 * An interface for an entity that requires asset manager assets
 */
public interface AssetConsumer {

    /**
     * Called to collect assets to load
     *
     * @param assetMultiplexer AssetManager to load assets
     */
    void loadAssets(AssetMultiplexer assetMultiplexer);

    /**
     * Called when assets are loaded
     *
     * @param assetMultiplexer AssetManager with assets
     */
    void assetsLoaded(AssetMultiplexer assetMultiplexer);
}
