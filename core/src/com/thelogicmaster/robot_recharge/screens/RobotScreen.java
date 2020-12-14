package com.thelogicmaster.robot_recharge.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.kiwi.util.tuple.immutable.Pair;
import com.thelogicmaster.robot_recharge.RobotUtils;
import com.thelogicmaster.robot_recharge.RobotRecharge;

import java.util.ArrayList;

public abstract class RobotScreen implements Screen {

    protected final InputMultiplexer inputMultiplexer;
    protected final OrthographicCamera uiCamera;
    protected final Stage stage;
    protected final Viewport uiViewport;
    protected final SpriteBatch spriteBatch;
    protected final AssetManager assetManager;
    protected final Skin skin;
    private final Array<Pair<String, Object>> debugValues;
    private final ArrayList<Disposable> disposables;
    private final GlyphLayout debugLayout;
    private boolean loaded;
    private Texture background;

    public RobotScreen() {
        skin = RobotRecharge.assets.skin;
        assetManager = RobotUtils.createAssetManager();
        spriteBatch = new SpriteBatch();
        inputMultiplexer = new InputMultiplexer();
        uiCamera = new OrthographicCamera();
        uiViewport = RobotUtils.createViewport(uiCamera);
        uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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

    protected final void setBackground(Texture background) {
        this.background = background;
        addDisposable(background);
    }

    protected void doneLoading() {

    }

    protected void draw(float delta) {
        stage.act(delta);
        stage.draw();

        if (debugValues.size > 0) {
            spriteBatch.begin();
            for (int i = 0; i < debugValues.size; i++) {
                debugLayout.setText(RobotRecharge.assets.fontNormal, debugValues.get(i).getFirst());
                RobotRecharge.assets.fontNormal.draw(spriteBatch, debugValues.get(i).getFirst() + ":", 0, 30 + (5 + RobotRecharge.assets.fontNormal.getLineHeight()) * i);
                String text = debugValues.get(i).getSecond() == null ? "null" : debugValues.get(i).getSecond().toString();
                RobotRecharge.assets.fontNormal.draw(spriteBatch, text, 10 + debugLayout.width, 30 + (5 + RobotRecharge.assets.fontNormal.getLineHeight()) * i);
            }
            debugValues.clear();
            spriteBatch.end();
        }
    }

    protected void drawLoading(float delta) {
    }

    protected boolean isDoneLoading() {
        return assetManager.update();
    }

    @Override
    public void render(float delta) {
        if (!loaded) {
            if (isDoneLoading()) {
                loaded = true;
                doneLoading();
            } else {
                drawLoading(delta);
                return;
            }
        }

        if (background != null) {
            spriteBatch.begin();
            spriteBatch.draw(background, 0, 0, uiViewport.getWorldWidth(), uiViewport.getWorldHeight());
            spriteBatch.end();
        }
        draw(delta);
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

    protected final <T extends Disposable> T addDisposable(T disposable) {
        disposables.add(disposable);
        return disposable;
    }

    @Override
    public void dispose() {
        for (Disposable disposable : disposables)
            disposable.dispose();
        assetManager.dispose();
        stage.dispose();
        spriteBatch.dispose();
    }
}
