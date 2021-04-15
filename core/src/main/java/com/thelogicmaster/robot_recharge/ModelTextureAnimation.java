package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ModelTextureAnimation implements AssetConsumer {

	@Getter @Setter private String material;
	@Getter @Setter private String atlas;
	@Getter @Setter private String animation;
	@Getter @Setter private float speed;

	private transient Animation<TextureRegion> animationInstance;
	private transient TextureAttribute textureAttribute;
	private transient float time;

	public ModelTextureAnimation (ModelTextureAnimation instance) {
		material = instance.material;
		atlas = instance.atlas;
		animation = instance.animation;
		speed = instance.speed;
		animationInstance = instance.animationInstance;
	}

	@Override
	public void loadAssets (AssetMultiplexer assetMultiplexer) {
		assetMultiplexer.load(atlas, TextureAtlas.class);
	}

	@Override
	public void assetsLoaded (AssetMultiplexer assetMultiplexer) {
		animationInstance = new Animation<>(speed, assetMultiplexer.<TextureAtlas>get(atlas).findRegions(animation), Animation.PlayMode.LOOP);
	}

	public void setup(ModelInstance instance) {
		Material materialInstance = instance.getMaterial(material);
		if (materialInstance == null) {
			Gdx.app.error("ModelTextureAnimation", "Failed to load material: " + material);
			return;
		}
		textureAttribute = materialInstance.get(TextureAttribute.class, TextureAttribute.Diffuse);
		Gdx.app.error("ModelTextureAnimation", "Failed to get texture attribute");
	}

	public void update() {
		time += Gdx.graphics.getDeltaTime();
		if (textureAttribute != null)
			textureAttribute.set(animationInstance.getKeyFrame(time));
	}
}
