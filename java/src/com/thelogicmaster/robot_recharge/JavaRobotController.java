package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.thelogicmaster.robot_recharge.*;
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
    private boolean fastForward;

    public JavaRobotController(Robot robot, RobotExecutionListener listener, CodeEngine engine) {
        this.engine = engine;
        this.listener = listener;
        this.robot = robot;
    }

    public void setFastForward(boolean fastForward) {
        this.fastForward = fastForward;
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
        robot.playAnimation("Armature|MoveForward");
        for (int i = 0; i < Math.abs(distance); i++) {
            Vector3 step = robot.getDirection().getVector().cpy().scl(Math.signum(distance) * Robot.speed * .01f);
            Position target = robot.getBlockPos().cpy().add(robot.getDirection().getVector().cpy().scl(Math.signum(distance)));
            if (robot.getLevel().isPositionInvalid(target) || (target.y > 0 && robot.getLevel().getBlock(target.cpy().add(0, -1, 0)) == null)) {
                robot.playAnimation(null);
                // Todo: play almost falling off animation for ledges
                return;
            } else if (robot.getLevel().getBlock(target) != null && robot.getLevel().getBlock(target).isSolid()) {
                robot.playAnimation(null);
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
        robot.playAnimation(null);
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
    public void speak(String message) {
        robot.textToSpeech(message);
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
    public boolean isRunning() {
        return running;
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
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
}
