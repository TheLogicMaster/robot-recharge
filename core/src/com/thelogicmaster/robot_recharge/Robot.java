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
import com.thelogicmaster.robot_recharge.code.ExecutionListener;
import com.thelogicmaster.robot_recharge.ui.TableDecal;

public class Robot implements IRobot, ExecutionListener, Disposable, Renderable3D {

    private volatile boolean running;
    private final Object lock = new Object();
    private volatile Thread thread;
    private String code;
    private final ModelInstance model;
    private final RobotExecutionListener listener;
    private final AnimationController animator;
    private volatile boolean fastForward;
    private volatile Vector3 position;
    private volatile Quaternion rotation;
    private volatile Position blockPos;
    private volatile Direction direction;
    private final Vector3 tempVec3 = new Vector3();
    private final Quaternion tempRot = new Quaternion();
    private final CodeEngine engine;
    private Level level;
    private final TableDecal dialog;
    private final Label message;

    private static final float speed = 2;
    private static final float rotationSpeed = 180;
    private static final Quaternion rotOffset = new Quaternion(Vector3.Y, 90);

    public Robot(ModelInstance model, RobotExecutionListener listener, CodeEngine engine, Viewport viewport) {
        this.model = model;
        this.engine = engine;
        animator = new AnimationController(model);
        this.listener = listener;
        code = "";
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
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setFastForward(boolean fast) {
        this.fastForward = fast;
    }

    public boolean isFastForward() {
        return fastForward;
    }

    public boolean isRunning() {
        return running;
    }

    public Position getBlockPos() {
        return blockPos;
    }

    public Direction getDirection() {
        return direction;
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
        stop();
        running = false;
        animator.setAnimation(null);
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    public void start() {
        running = true;
        if (thread != null) {
            synchronized (lock) {
                lock.notify();
            }
        } else
            thread = engine.run(this, code, this);
    }

    public void pause() {
        running = false;
    }

    @Override
    public void onExecutionInterrupted() {
        listener.onExecutionInterrupted();
    }

    @Override
    public void onExecutionFinish() {
        running = false;
        thread = null;
        listener.onExecutionFinish();
    }

    @Override
    public void onExecutionError(Exception e) {
        running = false;
        thread = null;
        listener.onExecutionError(e);
    }

    private void playAnimation(final String animation) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                animator.setAnimation(animation);
            }
        });
    }

    private void ensureRunning() throws InterruptedException {
        if (running)
            return;
        listener.onExecutionPaused();
        synchronized (lock) {
            lock.wait();
        }
    }

    @Override
    public void turn(int distance) throws InterruptedException {
        Quaternion step = new Quaternion(Vector3.Y, rotationSpeed * .01f * -Math.signum(distance));
        for (int i = 0; i < Math.abs(distance); i++) {
            double time = 90 / rotationSpeed;
            Direction target = Direction.fromYaw(direction.getQuaternion().getYaw() - distance * 90);
            while (time > 0) {
                ensureRunning();
                rotation.mul(step);
                Thread.sleep(fastForward ? 5 : 10);
                time -= .01f;
            }
            direction = target;
            rotation = direction.getQuaternion().cpy();
        }
    }

    @Override
    public void move(int distance) throws InterruptedException {
        playAnimation("Armature|MoveForward");
        Vector3 step = direction.getVector().cpy().scl(Math.signum(distance) * speed * .01f);
        for (int i = 0; i < Math.abs(distance); i++) {
            Position target = blockPos.cpy().add(direction.getVector().cpy().scl(Math.signum(distance)));
            if (level.isPositionInvalid(target) || (target.y > 0 && level.getBlock(target.cpy().add(0, -1, 0)) == null)) {
                playAnimation(null);
                // Todo: play almost falling off 'animation' for ledges
                return;
            } else if (level.getBlock(target) != null && level.getBlock(target).isSolid()) {
                playAnimation(null);
                level.onRobotCrash(this, target);
                // Todo: Play sound effect and move forward and back slightly to hit the block
                return;
            }
            double time = 1 / speed;
            while (time > 0) {
                ensureRunning();
                position.add(step);
                level.onRobotSubMove(this);
                Thread.sleep(fastForward ? 5 : 10);
                time -= .01f;
            }
            blockPos = target;
            blockPos.toVector(position);
            level.onRobotMove(this);
        }
        playAnimation(null);
    }

    @Override
    public void sleep(double duration) throws InterruptedException {
        double time = duration * 1000;
        while (time > 0) {
            ensureRunning();
            Thread.sleep(fastForward ? 5 : 10);
            time -= 10;
        }
    }

    @Override
    public void speak(final String message) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                RobotUtils.textToSpeech(message);
                Robot.this.message.setText(message);
                dialog.getTable().clearActions();
                dialog.getTable().addAction(Actions.sequence(
                        Actions.alpha(1, 0.25f),
                        Actions.delay(3),
                        Actions.alpha(0, 0.25f))
                );
            }
        });
    }

    @Override
    public void dispose() {
        dialog.dispose();
        stop();
    }
}
