package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ScrollingBackground {

	private float position;
	private final float speed;
	private final Texture foreground, background;

	public ScrollingBackground (String image) {
		this(image, 40);
	}

	public ScrollingBackground (String image, float speed) {
		this.speed = speed;

		foreground = new Texture(image + "-fg.png");
		background = new Texture(Gdx.files.internal(image + "-bg.png"), true);
		background.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
		background.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
	}

	public void draw (Batch batch, float delta) {
		position += speed * delta;
		position %= 1920 * 4;

		batch.draw(background, 0, 0, (int)position, 0, 1920, 1080);
		batch.draw(foreground, 0, 0);
	}
}
