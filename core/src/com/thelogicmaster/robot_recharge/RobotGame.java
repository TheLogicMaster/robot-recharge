package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.kotcrab.vis.ui.VisUI;
import com.thelogicmaster.robot_recharge.screens.GameScreen;

public class RobotGame extends Game {

	public static IJavaScriptEngine javaScriptEngine;
	public static IBlocklyEditor blocksEditor;
	public static BitmapFont fontNormal, fontLarge;

	public RobotGame(IJavaScriptEngine javaScriptEngine, IBlocklyEditor blocksEditor) {
		RobotGame.javaScriptEngine = javaScriptEngine;
		RobotGame.blocksEditor = blocksEditor;
	}

	@Override
	public void create () {
		VisUI.load(VisUI.SkinScale.X2);

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("monog.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 30;
		parameter.borderWidth = 1;
		parameter.color = Color.YELLOW;
		parameter.shadowOffsetX = 3;
		parameter.shadowOffsetY = 3;
		parameter.shadowColor = new Color(0, 0.5f, 0, 0.75f);
		fontNormal = generator.generateFont(parameter);
		parameter.size = 160;
		fontLarge = generator.generateFont(parameter);
		generator.dispose();

		setScreen(new GameScreen());
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
	}
}
