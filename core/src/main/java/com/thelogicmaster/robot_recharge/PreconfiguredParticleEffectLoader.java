package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;

/**
 * A 3D ParticleEffectLoader that automatically applies the provided ParticleEffectLoadParameter
 */
public class PreconfiguredParticleEffectLoader extends ParticleEffectLoader {

    private final ParticleEffectLoadParameter parameter;

    public PreconfiguredParticleEffectLoader(FileHandleResolver resolver, ParticleEffectLoadParameter parameter) {
        super(resolver);
        this.parameter = parameter;
    }

    public PreconfiguredParticleEffectLoader(ParticleEffectLoadParameter parameter) {
        this(new InternalFileHandleResolver(), parameter);
    }

    @Override
    public ParticleEffect loadSync(AssetManager manager, String fileName, FileHandle file, ParticleEffectLoadParameter parameter) {
        return super.loadSync(manager, fileName, file, this.parameter);
    }
}
