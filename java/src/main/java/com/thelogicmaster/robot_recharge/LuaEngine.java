package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Gdx;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.ExecutionListener;
import lombok.SneakyThrows;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.DebugLib;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaEngine implements CodeEngine {

    @Override
    public void initialize () {
    }

    @Override
    public ExecutionInstance run(final IRobot robot, final String code, final ExecutionListener listener) {
        Thread thread = new Thread(() -> {
            Globals globals = JsePlatform.standardGlobals();
            LuaValue luaRobot = CoerceJavaToLua.coerce(robot);
            globals.set("Robot", luaRobot);
            globals.load(new Watchdog());
            try {
                globals.load(code).call();
                listener.onExecutionFinish();
            } catch (LuaError e) {
                if (e.getCause() instanceof InterruptedException) {
                    listener.onExecutionInterrupted();
                    return;
                }
                Gdx.app.error("Lua", e.toString());
                listener.onExecutionError(e.getMessage());
            }
        });
        thread.start();
        return new ExecutionInstance(thread);
    }

    private static class Watchdog extends DebugLib {

        @Override
        @SneakyThrows
        public void onInstruction(int pc, Varargs v, int top) {
            if (Thread.interrupted())
                throw new InterruptedException();
            super.onInstruction(pc, v, top);
        }
    }
}
