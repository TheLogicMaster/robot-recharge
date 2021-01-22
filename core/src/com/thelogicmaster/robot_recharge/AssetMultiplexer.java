package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * An AssetManager multiplexer to combine multiple asset resolvers
 */
public class AssetMultiplexer implements Disposable {

    private final Array<AssetManager> assetManagers = new Array<>();

    public void add(AssetManager assetManager) {
        assetManagers.add(assetManager);
    }

    public AssetManager getManager(int index) {
        return assetManagers.get(index);
    }

    public synchronized boolean update() {
        boolean done = true;
        for (AssetManager assetManager : assetManagers)
            if (!assetManager.update())
                done = false;
        return done;
    }

    public synchronized <T> T get(String fileName) {
        for (AssetManager assetManager : assetManagers)
            if (assetManager.contains(fileName))
                return assetManager.get(fileName);
        throw new GdxRuntimeException("Asset not loaded: " + fileName);
    }

    public synchronized <T> T get(String fileName, Class<T> type) {
        for (AssetManager assetManager : assetManagers)
            if (assetManager.contains(fileName, type))
                return assetManager.get(fileName, type);
        throw new GdxRuntimeException("Asset not loaded: " + fileName);
    }

    public synchronized <T> Array<T> getAll(Class<T> type, Array<T> out) {
        for (AssetManager assetManager : assetManagers)
            assetManager.getAll(type, out);
        return out;
    }

    public synchronized <T> T get(AssetDescriptor<T> assetDescriptor) {
        return get(assetDescriptor.fileName, assetDescriptor.type);
    }

    public synchronized <T> void load(String fileName, Class<T> type) {
        load(fileName, type, null);
    }

    public synchronized <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
        for (AssetManager assetManager : assetManagers)
            if (assetManager.getFileHandleResolver().resolve(fileName).exists()) {
                if (parameter == null)
                    assetManager.load(fileName, type);
                else
                    assetManager.load(fileName, type, parameter);
                break;
            }
    }

    public synchronized void load(AssetDescriptor<?> desc) {
        load(desc.fileName, desc.type, desc.params);
    }

    public synchronized float getProgress() {
        float total = 0;
        for (AssetManager assetManager : assetManagers)
            total += assetManager.getProgress();
        return total / assetManagers.size;
    }

    @Override
    public void dispose() {
        for (AssetManager assetManager : assetManagers)
            assetManager.dispose();
    }
}
