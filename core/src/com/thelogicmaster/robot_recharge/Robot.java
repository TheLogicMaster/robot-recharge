package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.thelogicmaster.robot_recharge.code.ExecutionListener;
import com.thelogicmaster.robot_recharge.code.ICodeEngine;

public class Robot implements IRobot, ExecutionListener {

    private volatile boolean running;
    private final Object lock = new Object();
    private volatile Thread thread;
    private String code;
    private final ModelInstance model;
    private final RobotListener listener;
    private final AnimationController animator;
    private volatile boolean fastForward;
    private volatile Vector3 position;
    private volatile Quaternion rotation;
    private final Vector3 tempVec3;
    private final ICodeEngine engine;
    private Level level;

    private static final float speed = 2;
    private static final float rotationSpeed = 180;

    public Robot(ModelInstance model, RobotListener listener, ICodeEngine engine) {
        this.model = model;
        this.engine = engine;
        animator = new AnimationController(model);
        this.listener = listener;
        code = "";
        tempVec3 = new Vector3();
        position = new Vector3();
        rotation = new Quaternion();
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

    public void render(ModelBatch batch, Environment environment, float delta) {
        if (running)
            animator.update(fastForward ? 2 * delta : delta);
        rotation.nor();
        model.transform.set(tempVec3.set(position).add(Constants.blockOffset), rotation);
        batch.render(model, environment);
    }

    public void reset(Vector3 position, Quaternion rotation) {
        this.position = position;
        this.rotation = rotation;
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
        Quaternion start = rotation.cpy();
        double time = Math.abs(distance) * 90 / rotationSpeed;
        Quaternion step = new Quaternion(Vector3.Y, rotationSpeed * .01f * Math.signum(distance));
        while (time > 0) {
            ensureRunning();
            rotation.mul(step);
            Thread.sleep(fastForward ? 5 : 10);
            time -= .01f;
        }
        rotation = start.mul(new Quaternion(Vector3.Y, distance * 90));
    }

    @Override
    public void move(int distance) throws InterruptedException {
        playAnimation("Armature|MoveForward");
        Vector3 start = position.cpy();
        double time = Math.abs(distance) / speed;
        float dir = rotation.getYaw();
        if (distance < 0)
            dir += 180;
        float stepDist = speed * .01f;
        Vector3 step = new Vector3(MathUtils.sinDeg(dir) * stepDist, 0, MathUtils.cosDeg(dir) * stepDist);
        while (time > 0) {
            ensureRunning();
            position.add(step);
            Thread.sleep(fastForward ? 5 : 10);
            time -= .01f;
        }
        position.set(start.add(step.nor().scl(distance)));
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
}
