package com.thelogicmaster.robot_recharge.gwt;

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

    private static final int watchdogValue = 10000000;
    private static final String watchdogMessage = "Infinite Loop Detected (" + watchdogValue + " cycles exceeded)";

    private static Robot robot;
    private static RobotExecutionListener listener;
    private static boolean fastForward, paused, stopped, waiting;
    private static final Vector3 tempVec3 = new Vector3();
    private static final Quaternion tempQuaternion = new Quaternion();
    private static int calls;

    private String code;

    static {
        // Todo: Replace with reading a file and replacing constants with regex
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
                "       if ($wnd.Robot.checkFloor(distance)) {\n" +
                "           $wnd.Robot.playAnimationSpeed('Armature|Ledge', 0.3);\n" +
                "           await delayCheck(1500);\n" +
                "           return;\n" +
                "       }\n" +
                "       if ($wnd.Robot.checkCrash(distance)) {\n" +
                "           $wnd.Robot.playAnimationSpeed('Armature|Crash', 1);\n" +
                "           await delayCheck(100);\n" +
                "           $wnd.Robot.onCrash(distance);\n" +
                "           await delayCheck(500);\n" +
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
        $wnd.Robot.speak = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::speak(Ljava/lang/String;));
        $wnd.Robot.interact = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::interact());
        $wnd.Robot.move = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::move(I));
        $wnd.Robot.subMove = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::subMove(I));
        $wnd.Robot.turn = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::turn(I));
        $wnd.Robot.subTurn = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::subTurn(I));
        $wnd.Robot.playAnimation = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::playAnimation(Ljava/lang/String;));
        $wnd.Robot.playAnimationSpeed = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::playAnimationSpeed(Ljava/lang/String;F));
        $wnd.Robot.loopAnimation = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::loopAnimation(Ljava/lang/String;));
        $wnd.Robot.stopAnimation = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::stopAnimation());
        $wnd.Robot.isFast = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::isFast());
        $wnd.Robot.isStopped = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::isStopped());
        $wnd.Robot.isWaiting = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::isWaiting());
        $wnd.Robot.isPaused = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::isPaused());
        $wnd.Robot.onDone = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::onDone());
        $wnd.Robot.onError = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::onError(Ljava/lang/String;));
        $wnd.Robot.onPause = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::onPause());
        $wnd.Robot.onInterrupt = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::onInterrupt());
        $wnd.Robot.checkFloor = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::checkFloor(I));
        $wnd.Robot.checkCrash = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::checkCrash(I));
        $wnd.Robot.onCrash = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::onCrash(I));
        $wnd.Robot.incrementCalls = $entry(that.@com.thelogicmaster.robot_recharge.gwt.JavaScriptRobotController::incrementCalls());
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
        // Todo: Catch syntax errors somehow, possibly using eval around the whole thing
        ScriptInjector.fromString("" +
                "$wnd._watchdog = 0;\n" +
                "(async function(){\n" +
                code + "\n" +
                "})().then(interrupted => {\n" +
                "   if (interrupted)\n" +
                "       $wnd.Robot.onInterrupt();\n" +
                "   else\n" +
                "       $wnd.Robot.onDone();\n" +
                "}).catch(e => {\n" +
                "   $wnd.Robot.onError(e.toString());\n" +
                "});\n").inject();
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

    public void playAnimationSpeed(String animation, float speed) {
        robot.playAnimation(animation, 1, speed);
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

    public void onCrash(int distance) {
        Position target = robot.getBlockPos().cpy().add(robot.getDirection(), (int) Math.signum(distance));
        robot.getLevel().onRobotCrash(target);
    }

    public boolean checkCrash(int distance) {
        Position target = robot.getBlockPos().cpy().add(robot.getDirection(), (int) Math.signum(distance));
        return robot.getLevel().getBlock(target) != null && robot.getLevel().getBlock(target).isSolid();
    }

    public boolean checkFloor(int distance) {
        Position target = robot.getBlockPos().cpy().add(robot.getDirection(), (int) Math.signum(distance));
        Block floor = robot.getLevel().getBlock(target.cpy().add(0, -1, 0));
        return robot.getLevel().isPositionInvalid(target) || (target.y > 0 && (floor == null || !floor.isSolid()));
    }

    public void speak(String message) {
        incrementCalls();
        robot.textToSpeech(message);
    }

    public void interact() {
        incrementCalls();
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
        stopped = true;
        paused = false;
        listener.onExecutionFinish();
    }

    public void onInterrupt() {
        stopped = true;
        paused = false;
        listener.onExecutionInterrupted();
    }

    public void onError(String error) {
        stopped = true;
        paused = false;
        listener.onExecutionError(error);
    }

    public void onPause() {
        listener.onExecutionPaused();
    }

    // Calling this function from onError and onInterrupt causes weird errors
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

    private String transformForLoops(String code, boolean initializer, boolean incrementer) {
        return code.replaceAll("(?<=^|\\W)(for\\s*?\\(" + (initializer ? ".*?\\b.*?" : "\\s*?") + ")(;.*?;" +
                (incrementer ? ".*?\\b.*?" : "\\s*?") + ")(\\))", "$1" + (initializer ? "," : "") +
                "_watchdog=0$2" + (incrementer ? "," : "") + "(()=>{if(++_watchdog>" + watchdogValue + ")throw'" +
                watchdogMessage + "';})()$3");
    }

    @Override
    public void setCode(String code) {
        // Asynchronous functions
        code = code.replaceAll("(?<=^|\\W)(Robot\\.)((move|turn|sleep)\\(.+?\\))(;*)", "if(await $2){return true;}");

        // Synchronous functions
        code = code.replaceAll("(?<=^|\\W)(Robot\\.(speak|interact)\\(.+?\\))", "\\$wnd.$1");

        // for (var i = 0;; i < 10)
        code = transformForLoops(code, true, true);

        // for (var i = 0;;)
        code = transformForLoops(code, true, false);

        // for (;; i < 10)
        code = transformForLoops(code, false, true);

        // for (;;)
        code = transformForLoops(code, false, false);

        // While loops
        code = code.replaceAll("(?<=^|\\W)(while\\s*\\()(.*?)(\\))", "$1(($2)&&(()=>{if(++$wnd._watchdog>" + watchdogValue + ")throw'" +
                watchdogMessage + "';return true;})())||(()=>$wnd._watchdog=0)()$3");

        Gdx.app.debug("Generated Code", code);
        this.code = code;
    }
}
