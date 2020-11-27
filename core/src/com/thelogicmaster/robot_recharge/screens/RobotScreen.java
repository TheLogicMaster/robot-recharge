package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.kiwi.util.tuple.immutable.Pair;
import com.thelogicmaster.robot_recharge.Helpers;
import com.thelogicmaster.robot_recharge.RobotGame;

import java.util.ArrayList;

public abstract class RobotScreen implements Screen {

    protected final InputMultiplexer inputMultiplexer;
    protected final OrthographicCamera uiCamera;
    protected final Stage stage;
    protected final Viewport uiViewport;
    protected final SpriteBatch spriteBatch;
    protected final AssetManager assetManager;
    private final Array<Pair<String, Object>> debugValues;
    private final ArrayList<Disposable> disposables;
    private final GlyphLayout debugLayout;
    private boolean loaded;

    public RobotScreen() {
        assetManager = new AssetManager();
        spriteBatch = new SpriteBatch();
        inputMultiplexer = new InputMultiplexer();
        uiCamera = new OrthographicCamera();
        uiViewport = Helpers.createViewport(uiCamera);
        stage = new Stage(uiViewport);
        inputMultiplexer.addProcessor(stage);
        debugValues = new Array<>(true, 0);
        debugLayout = new GlyphLayout();
        disposables = new ArrayList<>();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    protected final void debugValue(String label, Object value) {
        debugValues.add(new Pair<>(label, value));
    }

    protected abstract void doneLoading();

    protected abstract void draw(float delta);

    protected void drawLoading(float delta) {
        spriteBatch.begin();
        RobotGame.fontLarge.draw(spriteBatch, "Loading...", 600, 400);
        spriteBatch.end();
    }

    protected boolean isDoneLoading() {
        return assetManager.update();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (!loaded) {
            if (isDoneLoading()) {
                loaded = true;
                doneLoading();
            }
            else {
                drawLoading(delta);
                return;
            }
        }

        draw(delta);

        stage.act(delta);
        stage.draw();

        if (debugValues.size > 0) {
            spriteBatch.begin();
            for (int i = 0; i < debugValues.size; i++) {
                debugLayout.setText(RobotGame.fontNormal, debugValues.get(i).getFirst());
                RobotGame.fontNormal.draw(spriteBatch, debugValues.get(i).getFirst() + ":", 0, 30 + (5 + RobotGame.fontNormal.getLineHeight()) * i);
                String text = debugValues.get(i).getSecond() == null ? "null" : debugValues.get(i).getSecond().toString();
                RobotGame.fontNormal.draw(spriteBatch, text, 10 + debugLayout.width, 30 + (5 + RobotGame.fontNormal.getLineHeight()) * i);
            }
            debugValues.clear();
            spriteBatch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
        spriteBatch.setProjectionMatrix(uiCamera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    protected final void addDisposable(Disposable disposable) {
        disposables.add(disposable);
    }

    @Override
    public void dispose() {
        for (Disposable disposable: disposables)
            disposable.dispose();
        assetManager.dispose();
        stage.dispose();
    }
}
