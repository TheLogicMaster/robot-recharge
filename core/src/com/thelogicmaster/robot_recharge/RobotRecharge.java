package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Json;
import com.kotcrab.vis.ui.VisUI;
import com.thelogicmaster.robot_recharge.screens.GameScreen;
import com.thelogicmaster.robot_recharge.screens.TitleScreen;

public class RobotRecharge extends Game {

    public static IJavaScriptEngine javaScriptEngine;
    public static IBlocklyEditor blocksEditor;
    public static BitmapFont fontNormal, fontLarge;
    public static RobotRecharge instance;

    private TitleScreen titleScreen;

    public RobotRecharge(IJavaScriptEngine javaScriptEngine, IBlocklyEditor blocksEditor) {
        RobotRecharge.javaScriptEngine = javaScriptEngine;
        RobotRecharge.blocksEditor = blocksEditor;
    }

    @Override
    public void create() {
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

        Gdx.files.local("save/").mkdirs();

        LevelData levelData = new LevelData("test", true, "<xml xmlns=\"https://developers.google.com/blockly/xml\"><block type=\"controls_whileUntil\" id=\")a]s" +
                "GO7-T5IP%$HIvB2L\" x=\"134\" y=\"84\"><field name=\"MODE\">UNTIL</field><statement name=\"DO\"><block " +
                "type=\"robot_move\" id=\"S75yeoZ`/e5Rv%F}%SX1\"><value name=\"distance\"><block type=\"math_number\" id" +
                "=\"-r+,j4?5V]f0%z.QD^p{\"><field name=\"NUM\">1</field></block></value><next><block type=\"robot_sleep" +
                "\" id=\"Gj4[f%K@Z2!%wgnk2M`X\"><value name=\"duration\"><block type=\"math_number\" id=\"TwlZ]Yvqp=If;5" +
                "BO{bPE\"><field name=\"NUM\">1</field></block></value><next><block type=\"robot_turn\" id=\"K)AbVjj1gr)" +
                "OR6r*q[th\"><value name=\"distance\"><block type=\"math_number\" id=\"jo)d*8|gyjwC0p:baO#5\"><field nam" +
                "e=\"NUM\">1</field></block></value></block></next></block></next></block></statement></block></xml>", Language.JavaScript);
        FileHandle save = Gdx.files.internal("save/test.txt");
        if (save.exists())
            levelData = new Json().fromJson(LevelData.class, save);
        setScreen(new GameScreen(levelData));
        titleScreen = new TitleScreen();
        //setScreen(titleScreen);
    }

    public void returnToTitle() {
        setScreen(titleScreen);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        fontLarge.dispose();
        fontNormal.dispose();
    }
}
