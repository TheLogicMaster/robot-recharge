package com.thelogicmaster.robot_recharge.structures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.Level;
import lombok.Getter;
import lombok.Setter;
import lombok.var;

@Getter
@Setter
public class Wire extends Structure {

    private final static float size = 0.1f; // Wire diameter
    private final static Vector3 offset = new Vector3(size, 0, size);

    private Array<Vector3> path = new Array<>();
    private Color color = Color.RED;
    private String id;

    private transient ModelInstance model;
    private transient Pixmap pixmap;

    @Override
    public void generate(Level level) {
        super.generate(level);
        ModelBuilder builder = new ModelBuilder();
        builder.begin();
        Material material = new Material();
        pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.setColor(color);
        pixmap.drawPixel(0, 0);
        material.set(TextureAttribute.createDiffuse(new Texture(pixmap)));
        Vector3 previousDirection = null;
        for (int i = 0; i < path.size - 1; i++) {
            var direction = path.get(i + 1).cpy().sub(path.get(i)).nor();
            var rotation = new Quaternion().setFromCross(Vector3.Y, direction);
            var start = path.get(i).cpy();

            // Just a bit of "magic" to ensure the corners meet
            if (i != 0 && ((previousDirection.x > 0 && direction.z < 0) || (previousDirection.x > 0 && direction.y < 0)
                    || (previousDirection.y > 0 && direction.x < 0) || (previousDirection.z > 0 && direction.x < 0)))
                start.add(new Vector3(0, (direction.y < 0 ? 1 : -1) * size, 0).mul(rotation));

            var end = offset.cpy().mul(rotation).add(path.get(i + 1));
            if (direction.x > 0 || direction.z > 0) // Y offset is inverted for South/East facing wires
                end.add(new Vector3(0, 2 * size, 0));

            BoxShapeBuilder.build(builder.part("Segment " + (i + 1), GL20.GL_TRIANGLES,
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), new BoundingBox(start, end));
            previousDirection = direction.cpy();
        }

        model = new ModelInstance(builder.end());

        level.addLevelListener(event -> Gdx.app.postRunnable(() -> {
            if (!event.getId().equals(id))
                return;
            if ("on".equals(event.getEvent()))
                model.materials.get(0).set(TextureAttribute.createEmissive(new Texture(pixmap)));
            else if ("off".equals(event.getEvent()))
                model.materials.get(0).remove(TextureAttribute.Emissive);
        }));
    }

    @Override
    public void render(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment, float delta) {
        super.render(modelBatch, decalBatch, environment, delta);
        modelBatch.render(model, environment);
    }

    @Override
    public void dispose() {
        if (model != null)
            model.model.dispose();
        if (pixmap != null)
            pixmap.dispose();
    }
}
