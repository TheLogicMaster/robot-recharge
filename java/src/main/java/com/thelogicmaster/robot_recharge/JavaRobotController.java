package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.thelogicmaster.robot_recharge.blocks.Block;
import com.thelogicmaster.robot_recharge.blocks.Interactable;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.ExecutionListener;
import com.thelogicmaster.robot_recharge.code.IExecutionInstance;

// Todo: Move Threaded classes to dedicated Java module for GWT compilation purposes
@SuppressWarnings("BusyWait")
public class JavaRobotController implements RobotController, ExecutionListener, IRobot {

    private volatile boolean running; // Code is currently running
    private volatile boolean waiting; // Waiting for something in the level like an elevator
    private final Object lock = new Object();
    private volatile IExecutionInstance executionInstance;
    private final CodeEngine engine;
    private final RobotExecutionListener listener;
    private final Robot robot;
    private String code;
    private volatile int calls;
    private volatile boolean fastForward;

    public JavaRobotController(Robot robot, RobotExecutionListener listener, CodeEngine engine) {
        this.engine = engine;
        this.listener = listener;
        this.robot = robot;
    }

    @Override
    public void setFastForward(boolean fastForward) {
        this.fastForward = fastForward;
    }

    @Override
    public int getCalls() {
        return calls;
    }

    private synchronized void incrementCalls() {
        calls++;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private void ensureRunning() throws InterruptedException {
        if (executionInstance == null)
            throw new InterruptedException();
        if (running && !waiting)
            return;
        if (!running)
            listener.onExecutionPaused();
        lockThread();
    }

    @Override
    public void turn(int distance) throws InterruptedException {
        incrementCalls();
        for (int i = 0; i < Math.abs(distance); i++) {
            Quaternion step = new Quaternion(Vector3.Y, Robot.rotationSpeed * .01f * -Math.signum(distance));
            double time = 90 / Robot.rotationSpeed;
            Direction target = Direction.fromYaw(robot.getDirection().getQuaternion().getYaw() - Math.signum(distance) * 90);
            while (time > 0) {
                ensureRunning();
                robot.getRotation().mul(step);
                Thread.sleep(fastForward ? 5 : 10);
                time -= .01f;
            }
            robot.setDirection(target);
            robot.getRotation().set(robot.getDirection().getQuaternion().cpy());
        }
    }

    private void delay(double duration) throws InterruptedException {
        double time = duration * 1000;
        while (time > 0) {
            ensureRunning();
            Thread.sleep(fastForward ? 5 : 10);
            time -= 10;
        }
    }

    @Override
    public void move(int distance) throws InterruptedException {
        incrementCalls();
        robot.loopAnimation("Armature|MoveForward");
        for (int i = 0; i < Math.abs(distance); i++) {
            ensureRunning();
            Vector3 step = robot.getDirection().getVector().cpy().scl(Math.signum(distance) * Robot.speed * .01f);
            Position target = robot.getBlockPos().cpy().add(robot.getDirection().getVector().cpy().scl(Math.signum(distance)));
            Block floor = robot.getLevel().getBlock(target.cpy().add(0, -1, 0));
            if (robot.getLevel().isPositionInvalid(target) || (target.y > 0 && (floor == null || !floor.isSolid()))) {
                robot.playAnimation("Armature|Ledge");
                delay(1);
                // Todo: play sound effect
                return;
            } else if (robot.getLevel().getBlock(target) != null && robot.getLevel().getBlock(target).isSolid()) {
                if (i == 0)
                    robot.playAnimation("Armature|Crash");
                else
                    robot.getAnimator().animate("Armature|Crash", .05f);
                delay(0.1);
                robot.getLevel().onRobotCrash(target);
                delay(0.5);
                // Todo: Play sound effect and fix animation blending
                return;
            }
            double time = 1 / Robot.speed;
            while (time > 0) {
                ensureRunning();
                robot.getPosition().add(step);
                robot.getLevel().onRobotSubMove();
                Thread.sleep(fastForward ? 5 : 10);
                time -= .01f;
            }
            robot.setPosition(target);
        }
        robot.stopAnimation();
    }

    @Override
    public void sleep(double duration) throws InterruptedException {
        incrementCalls();
        delay(duration);
    }

    @Override
    public void speak(String message) {
        incrementCalls();
        // Todo: Causes freeze when called in infinite loop
        robot.textToSpeech(message);
    }

    @Override
    public void interact() {
        incrementCalls();
        Block block = robot.getLevel().getBlock(robot.getBlockPos().cpy().add(robot.getDirection().getVector()));
        if (block instanceof Interactable)
            ((Interactable) block).interact(robot);
    }

    private void lockThread() throws InterruptedException {
        synchronized (lock) {
            lock.wait();
        }
    }

    private void unlockThread() {
        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public void start() {
        running = true;
        if (executionInstance != null) {
            if (!waiting)
                unlockThread();
        } else {
            calls = 0;
            executionInstance = engine.run(this, code, this);
            waiting = false;
        }
    }

    @Override
    public void pause() {
        running = false;
        if (waiting)
            listener.onExecutionPaused();
    }

    @Override
    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
        if (!waiting)
            unlockThread();
    }

    @Override
    public boolean isWaiting() {
        return waiting;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void stop() {
        if (executionInstance != null)
            executionInstance.stop();
        executionInstance = null;
    }

    private void reset() {
        running = false;
        executionInstance = null;
    }

    @Override
    public void onExecutionInterrupted() {
        reset();
        listener.onExecutionInterrupted();
    }

    @Override
    public void onExecutionFinish() {
        reset();
        listener.onExecutionFinish();
    }

    @Override
    public void onExecutionError(String error) {
        reset();
        listener.onExecutionError(error);
    }
}
