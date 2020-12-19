package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * A decal that displays a Scene2D table
 * Can optionally act as a billboard by facing the camera
 */
public class TableDecal extends Decal implements Disposable {

    private final TextureRegion textureRegion;
    private final FrameBuffer buffer;
    private final Table table;
    private final Vector3 tempVec3 = new Vector3();
    private final Viewport viewport;
    private boolean billboard;
    private final SpriteBatch batch;

    /**
     * @param viewport  The perspective camera's viewport
     * @param width     The width of the decal in world coordinates
     * @param height    The height of the decal in world coordinates
     * @param billboard Whether it should turn towards to camera or not
     */
    public TableDecal(Viewport viewport, int stageWidth, int stageHeight, int width, int height, boolean billboard) {
        this(viewport, new Table(), stageWidth, stageHeight, width, height, billboard);
    }

    /**
     * @param viewport    The perspective camera's viewport
     * @param table       The table to use
     * @param tableWidth  The width of the decal stage
     * @param tableHeight The height of the decal stage
     * @param width       The width of the decal in world coordinates
     * @param height      The height of the decal in world coordinates
     * @param billboard   Whether it should turn towards to camera or not
     */
    public TableDecal(Viewport viewport, Table table, int tableWidth, int tableHeight, int width, int height, boolean billboard) {
        this.billboard = billboard;
        this.viewport = viewport;
        setDimensions(width, height);
        setColor(1, 1, 1, 1);
        setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        buffer = new FrameBuffer(Pixmap.Format.RGBA8888, tableWidth, tableHeight, false);
        textureRegion = new TextureRegion(buffer.getColorBufferTexture());
        textureRegion.flip(false, true);
        this.table = table;
        table.setSize(tableWidth, tableHeight);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, tableWidth, tableHeight));
    }

    /**
     * Draw the decal
     *
     * @param decalBatch The DecalBatch to add decal to
     * @param delta      The delta time
     */
    public void draw(DecalBatch decalBatch, float delta) {
        table.act(delta);

        if (!table.isVisible() || table.getColor().a == 0f)
            return;

        buffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        table.draw(batch, 1);
        batch.end();
        buffer.end();
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        setTextureRegion(textureRegion);
        decalBatch.add(this);

        if (billboard)
            lookAt(tempVec3.set(viewport.getCamera().position.x, getY(), viewport.getCamera().position.z), Vector3.Y);
    }

    public boolean isBillboard() {
        return billboard;
    }

    public void setBillboard(boolean billboard) {
        this.billboard = billboard;
    }

    public Table getTable() {
        return table;
    }

    @Override
    public void dispose() {
        buffer.dispose();
        batch.dispose();
    }
}
