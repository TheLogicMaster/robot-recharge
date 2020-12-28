package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.thelogicmaster.robot_recharge.blocks.Block;
import com.thelogicmaster.robot_recharge.blocks.Interactable;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.ExecutionListener;

public class JavaRobotController implements RobotController, ExecutionListener, IRobot {

    private volatile boolean running;
    private final Object lock = new Object();
    private volatile Thread thread;
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

    @Override
    public void setCode(String code) {
        this.code = code;
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
        incrementCalls();
        for (int i = 0; i < Math.abs(distance); i++) {
            Quaternion step = new Quaternion(Vector3.Y, Robot.rotationSpeed * .01f * -Math.signum(distance));
            double time = 90 / Robot.rotationSpeed;
            Direction target = Direction.fromYaw(robot.getDirection().getQuaternion().getYaw() - distance * 90);
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

    @Override
    public void move(int distance) throws InterruptedException {
        incrementCalls();
        robot.loopAnimation("Armature|MoveForward");
        for (int i = 0; i < Math.abs(distance); i++) {
            Vector3 step = robot.getDirection().getVector().cpy().scl(Math.signum(distance) * Robot.speed * .01f);
            Position target = robot.getBlockPos().cpy().add(robot.getDirection().getVector().cpy().scl(Math.signum(distance)));
            if (robot.getLevel().isPositionInvalid(target) || (target.y > 0 && robot.getLevel().getBlock(target.cpy().add(0, -1, 0)) == null)) {
                robot.stopAnimation();
                // Todo: play almost falling off animation for ledges
                return;
            } else if (robot.getLevel().getBlock(target) != null && robot.getLevel().getBlock(target).isSolid()) {
                robot.stopAnimation();
                robot.getLevel().onRobotCrash(robot, target);
                // Todo: Play sound effect and crash animation
                return;
            }
            double time = 1 / Robot.speed;
            while (time > 0) {
                ensureRunning();
                robot.getPosition().add(step);
                robot.getLevel().onRobotSubMove(robot);
                Thread.sleep(fastForward ? 5 : 10);
                time -= .01f;
            }
            robot.getBlockPos().set(target);
            target.toVector(robot.getPosition());
            robot.getLevel().onRobotMove(robot);
        }
        robot.stopAnimation();
    }

    @Override
    public void sleep(double duration) throws InterruptedException {
        incrementCalls();
        double time = duration * 1000;
        while (time > 0) {
            ensureRunning();
            Thread.sleep(fastForward ? 5 : 10);
            time -= 10;
        }
    }

    @Override
    public void speak(String message) {
        incrementCalls();
        robot.textToSpeech(message);
    }

    @Override
    public void interact() {
        incrementCalls();
        Block block = robot.getLevel().getBlock(robot.getBlockPos().cpy().add(robot.getDirection().getVector()));
        if (block instanceof Interactable)
            ((Interactable) block).interact(robot);
    }

    public void start() {
        running = true;
        if (thread != null) {
            synchronized (lock) {
                lock.notify();
            }
        } else {
            calls = 0;
            thread = engine.run(this, code, this);
        }
    }

    public void pause() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    private void reset() {
        running = false;
        thread = null;
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
    public void onExecutionError(Exception e) {
        reset();
        listener.onExecutionError(e);
    }
}
