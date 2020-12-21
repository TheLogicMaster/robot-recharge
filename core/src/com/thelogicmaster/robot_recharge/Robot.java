package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.ui.TableDecal;

public class Robot implements Disposable, Renderable3D {

    private final ModelInstance model;
    private final AnimationController animator;
    private volatile boolean fastForward;
    private volatile Vector3 position;
    private volatile Quaternion rotation;
    private volatile Position blockPos;
    private volatile Direction direction;
    private final Vector3 tempVec3 = new Vector3();
    private final Quaternion tempRot = new Quaternion();
    private Level level;
    private final TableDecal dialog;
    private final Label message;
    private final RobotController robot;

    public static final float speed = 2;
    public static final float rotationSpeed = 180;
    private static final Quaternion rotOffset = new Quaternion(Vector3.Y, 90);

    public Robot(ModelInstance model, RobotExecutionListener listener, CodeEngine engine, Viewport viewport) {
        this.model = model;
        animator = new AnimationController(model);
        position = new Vector3();
        rotation = new Quaternion();
        blockPos = new Position();
        direction = Direction.NORTH;
        Table table = new Table(RobotRecharge.assets.skin);
        dialog = new TableDecal(viewport, table, 256, 256, 1, 1, true);
        table.setBackground("robotDialogTen");
        table.pad(10);
        table.getColor().a = 0;
        message = new Label("", RobotRecharge.assets.skin);
        table.add(message).grow();
        robot = RobotRecharge.platformUtils.createRobot(this, listener, engine);
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public void setCode(String code) {
        robot.setCode(code);
    }

    public void setFastForward(boolean fast) {
        this.fastForward = fast;
        robot.setFastForward(fast);
    }

    public boolean isFastForward() {
        return fastForward;
    }

    public boolean isRunning() {
        return robot.isRunning();
    }

    public Position getBlockPos() {
        return blockPos;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void render(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment, float delta) {
        animator.update(fastForward ? 2 * delta : delta);
        rotation.nor();
        model.transform.set(tempVec3.set(position).add(Constants.blockOffset), tempRot.set(rotation).mul(rotOffset));
        modelBatch.render(model, environment);
        dialog.setPosition(tempVec3.set(position).add(0, 1, 0).add(Constants.blockOffset));
        dialog.draw(decalBatch, delta);
    }

    public void reset(Position position, Direction direction) {
        blockPos = position;
        this.position = blockPos.toVector(new Vector3());
        this.rotation = direction.getQuaternion().cpy();
        this.direction = direction;
        robot.stop();
        animator.setAnimation(null);
        dialog.getTable().clearActions();
        dialog.getTable().getColor().a = 0;
    }

    public void pause() {
        robot.pause();
    }

    public void start() {
        robot.start();
    }

    public void textToSpeech(final String message) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                RobotUtils.textToSpeech(message);
                Robot.this.message.setText(message);
                dialog.getTable().clearActions();
                dialog.getTable().getColor().a = 1;
                dialog.getTable().addAction(Actions.sequence(
                        Actions.delay(3),
                        Actions.alpha(0, 0.25f))
                );
            }
        });
    }

    public void playAnimation(final String animation) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                animator.setAnimation(animation);
            }
        });
    }

    @Override
    public void dispose() {
        dialog.dispose();
        robot.stop();
    }
}
