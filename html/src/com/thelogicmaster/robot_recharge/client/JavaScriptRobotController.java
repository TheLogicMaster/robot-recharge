package com.thelogicmaster.robot_recharge.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.google.gwt.core.client.ScriptInjector;
import com.thelogicmaster.robot_recharge.*;
import com.thelogicmaster.robot_recharge.blocks.Block;
import com.thelogicmaster.robot_recharge.blocks.Interactable;

@SuppressWarnings("unused")
public class JavaScriptRobotController implements RobotController {
    // Static fields to circumvent weird js context issue or something

    private static Robot robot;
    private static RobotExecutionListener listener;
    private static boolean fastForward, paused, stopped, waiting;
    private static final Vector3 tempVec3 = new Vector3();
    private static final Quaternion tempQuaternion = new Quaternion();
    private static int calls;

    private String code;

    static {
        ScriptInjector.fromString("" +
                "let delay = m => new Promise(r => setTimeout(r, m));\n" +

                "async function delayCheck(duration) {\n" +
                "   let time = duration;\n" +
                "   let isPaused = false;\n" +
                "   while (time > 0) {\n" +
                "       await delay($wnd.Robot.isFast() ? 10 : 20);\n" +
                "       if ($wnd.Robot.isStopped())\n" +
                "           return true;\n" +
                "       if (!$wnd.Robot.isPaused()) {\n" +
                "           if (!$wnd.Robot.isWaiting())\n" +
                "               time -= 20;\n" +
                "       } else if (!isPaused)\n" +
                "           $wnd.Robot.onPause();\n" +
                "       isPaused = $wnd.Robot.isPaused();\n" +
                "   }\n" +
                "}\n" +

                "async function move(distance) {\n" +
                "   $wnd.Robot.incrementCalls();\n" +
                "   $wnd.Robot.loopAnimation('Armature|MoveForward');\n" +
                "   for (let i = 0; i < Math.abs(distance); i++) {\n" +
                "       if (await delayCheck(1))\n" +
                "           return true;\n" +
                "       if ($wnd.Robot.checkCrash(distance) || $wnd.Robot.checkFloor(distance)) {\n" +
                "           $wnd.Robot.stopAnimation();\n" +
                "           return;\n" +
                "       }\n" +
                "       let time = 1 / " + Robot.speed + ";\n" +
                "       while (time > 0) {\n" +
                "           $wnd.Robot.subMove(distance);\n" +
                "           if (await delayCheck(20))\n" +
                "               return true;\n" +
                "           time -= 0.02;\n" +
                "       }\n" +
                "       $wnd.Robot.move(distance);\n" +
                "   }\n" +
                "   $wnd.Robot.stopAnimation();\n" +
                "}\n" +

                "async function turn(distance) {\n" +
                "   $wnd.Robot.incrementCalls();\n" +
                "   for (let i = 0; i < Math.abs(distance); i++) {\n" +
                "       let time = 90 / " + Robot.rotationSpeed + ";\n" +
                "       while (time > 0) {\n" +
                "           $wnd.Robot.subTurn(distance);\n" +
                "           if (await delayCheck(20))\n" +
                "               return true;\n" +
                "           time -= 0.02;\n" +
                "       }\n" +
                "       $wnd.Robot.turn(distance);\n" +
                "   }\n" +
                "}\n" +

                "async function sleep(duration) {\n" +
                "   $wnd.Robot.incrementCalls();\n" +
                "   await delayCheck(duration * 1000);\n" +
                "}\n"
        ).inject();
    }

    public JavaScriptRobotController(Robot robot, RobotExecutionListener listener) {
        JavaScriptRobotController.robot = robot;
        JavaScriptRobotController.listener = listener;
        code = "";
        inject();
    }

    private native void inject() /*-{
        var that = this;
        $wnd.Robot = new Object();
        $wnd.Robot.speak = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::speak(Ljava/lang/String;));
        $wnd.Robot.interact = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::interact());
        $wnd.Robot.move = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::move(I));
        $wnd.Robot.subMove = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::subMove(I));
        $wnd.Robot.turn = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::turn(I));
        $wnd.Robot.subTurn = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::subTurn(I));
        $wnd.Robot.playAnimation = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::playAnimation(Ljava/lang/String;));
        $wnd.Robot.loopAnimation = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::loopAnimation(Ljava/lang/String;));
        $wnd.Robot.stopAnimation = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::stopAnimation());
        $wnd.Robot.isFast = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::isFast());
        $wnd.Robot.isStopped = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::isStopped());
        $wnd.Robot.isWaiting = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::isWaiting());
        $wnd.Robot.isPaused = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::isPaused());
        $wnd.Robot.onDone = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::onDone());
        $wnd.Robot.onPause = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::onPause());
        $wnd.Robot.onInterrupt = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::onInterrupt());
        $wnd.Robot.checkFloor = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::checkFloor(I));
        $wnd.Robot.checkCrash = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::checkCrash(I));
        $wnd.Robot.incrementCalls = $entry(that.@com.thelogicmaster.robot_recharge.client.JavaScriptRobotController::incrementCalls());
    }-*/;

    @Override
    public void start() {
        if (paused) {
            paused = false;
            return;
        }
        stopped = false;
        calls = 0;
        waiting = false;
        try {
            ScriptInjector.fromString("" +
                    "(async function(){\n" +
                    code + "\n" +
                    "})().then(interrupted => {\n" +
                    "   if (interrupted)\n" +
                    "       $wnd.Robot.onInterrupt();\n" +
                    "   else\n" +
                    "       $wnd.Robot.onDone();\n" +
                    "})\n").inject();
        } catch (Exception e) {
            Gdx.app.error("JavaScriptRobotController", "Execution error", e);
            listener.onExecutionError(e);
        }
    }

    public void move(int distance) {
        Position target = robot.getBlockPos().cpy().add(robot.getDirection().getVector().cpy().scl(Math.signum(distance)));
        robot.getBlockPos().set(target);
        target.toVector(robot.getPosition());
        robot.getLevel().onRobotMove();
    }

    public void subMove(int distance) {
        Vector3 step = tempVec3.set(robot.getDirection().getVector()).scl(Math.signum(distance) * Robot.speed * .02f);
        robot.getPosition().add(step);
        robot.getLevel().onRobotSubMove();
    }

    public void turn(int distance) {
        Direction target = Direction.fromYaw(robot.getDirection().getQuaternion().getYaw() - Math.signum(distance) * 90);
        robot.setDirection(target);
        robot.getRotation().set(robot.getDirection().getQuaternion().cpy());
    }

    public void subTurn(int distance) {
        Quaternion step = tempQuaternion.set(Vector3.Y, Robot.rotationSpeed * .02f * -Math.signum(distance));
        robot.getRotation().mul(step);
    }

    public void playAnimation(String animation) {
        robot.playAnimation(animation);
    }

    public void loopAnimation(String animation) {
        robot.loopAnimation(animation);
    }

    public void stopAnimation() {
        robot.stopAnimation();
    }

    public void incrementCalls() {
        calls++;
    }

    public boolean checkCrash(int distance) {
        Position target = robot.getBlockPos().cpy().add(robot.getDirection().getVector().cpy().scl(Math.signum(distance)));
        boolean crash = robot.getLevel().getBlock(target) != null && robot.getLevel().getBlock(target).isSolid();
        if (crash)
            robot.getLevel().onRobotCrash(target);
        return crash;
    }

    public boolean checkFloor(int distance) {
        Position target = robot.getBlockPos().cpy().add(robot.getDirection().getVector().cpy().scl(Math.signum(distance)));
        return robot.getLevel().isPositionInvalid(target) || (target.y > 0 && robot.getLevel().getBlock(target.cpy().add(0, -1, 0)) == null);
    }

    public void speak(String message) {
        calls++;
        robot.textToSpeech(message);
    }

    public void interact() {
        calls++;
        Block block = robot.getLevel().getBlock(robot.getBlockPos().cpy().add(robot.getDirection().getVector()));
        if (block instanceof Interactable)
            ((Interactable) block).interact(robot);
    }

    public boolean isFast() {
        return fastForward;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void onDone() {
        listener.onExecutionFinish();
        stopped = true;
        paused = false;
    }

    public void onInterrupt() {
        listener.onExecutionInterrupted();
        stopped = true;
        paused = false;
    }

    public void onPause() {
        listener.onExecutionPaused();
    }

    @Override
    public void stop() {
        stopped = true;
        paused = false;
    }

    @Override
    public void setWaiting(boolean waiting) {
        JavaScriptRobotController.waiting = waiting;
    }

    @Override
    public boolean isWaiting() {
        return waiting;
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void setFastForward(boolean fastForward) {
        JavaScriptRobotController.fastForward = fastForward;
    }

    @Override
    public boolean isRunning() {
        return !stopped && !paused;
    }

    @Override
    public int getCalls() {
        return calls;
    }

    @Override
    public void setCode(String code) {
        code = code.replaceAll("(Robot\\.)((move|turn|sleep)\\(.+?\\))(;*)", "if (await $2) return true;");
        code = code.replaceAll("(Robot\\.(speak|interact)\\(.+?\\))", "\\$wnd.$1");
        this.code = code;
    }
}
