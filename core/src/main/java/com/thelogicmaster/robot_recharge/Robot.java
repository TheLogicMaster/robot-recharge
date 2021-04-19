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
    private final Level level;
    private final TableDecal dialog;
    private final Label message;
    private final RobotController controller;

    public static final float speed = 2;
    public static final float rotationSpeed = 180;
    private static final Quaternion rotOffset = new Quaternion(Vector3.Y, 90);

    public Robot(ModelInstance model, CodeEngine engine, Viewport viewport, Level level) {
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
        controller = RobotRecharge.platformUtils.createRobotController(this, level, engine);
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

    public void setPosition(Position position) {
        setPosition(position, false);
    }

    public void setPosition(Position position, boolean silent) {
        this.blockPos = position;
        this.blockPos.toVector(this.position);

        if (!silent)
            level.onRobotMove();
    }

    public void setCode(String code) {
        controller.setCode(code);
    }

    public void setFastForward(boolean fast) {
        this.fastForward = fast;
        controller.setFastForward(fast);
    }

    public void setWaiting(boolean waiting) {
        controller.setWaiting(waiting);
    }

    public boolean isFastForward() {
        return fastForward;
    }

    public boolean isRunning() {
        return controller.isRunning();
    }

    public Position getBlockPos() {
        return blockPos;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
        rotation = direction.getQuaternion().cpy();
    }

    @Override
    public void render(ModelBatch modelBatch, DecalBatch decalBatch, Environment environment, float delta) {
        if (!isRunning())
            delta = 0;
        if (!controller.isWaiting())
            animator.update(delta);
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
        controller.stop();
        animator.setAnimation(null);
        dialog.getTable().clearActions();
        dialog.getTable().getColor().a = 0;
    }

    public void stop() {
        controller.stop();
    }

    public void pause() {
        controller.pause();
    }

    public void start() {
        controller.start();
    }

    public void textToSpeech(final String message) {
        Gdx.app.postRunnable(() -> {
            RobotUtils.textToSpeech(message);
            Robot.this.message.setText(message);
            dialog.getTable().clearActions();
            dialog.getTable().getColor().a = 1;
            dialog.getTable().addAction(Actions.sequence(
                    Actions.delay(3),
                    Actions.alpha(0, 0.25f))
            );
        });
    }

    public void playAnimation(final String animation, final int loopCount, final float speed) {
        Gdx.app.postRunnable(() -> animator.setAnimation(animation, loopCount, speed, null));
    }

    public void playAnimation(final String animation, final int loopCount) {
        playAnimation(animation, loopCount, 1);
    }

    public void playAnimation(String animation) {
        playAnimation(animation, 1);
    }

    public void loopAnimation(String animation) {
        playAnimation(animation, -1);
    }

    public void blendAnimation(final String animation, final float blendTime) {
        Gdx.app.postRunnable(() -> animator.animate(animation, blendTime));
    }

    public void stopAnimation() {
        animator.setAnimation(null);
    }

    public AnimationController getAnimator() {
        return animator;
    }

    public int getCalls() {
        return controller.getCalls();
    }

    @Override
    public void dispose() {
        dialog.dispose();
        controller.stop();
    }
}
