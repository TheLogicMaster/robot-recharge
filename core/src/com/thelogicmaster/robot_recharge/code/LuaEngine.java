package com.thelogicmaster.robot_recharge.code;

import com.badlogic.gdx.Gdx;
import com.thelogicmaster.robot_recharge.IRobot;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaEngine implements ICodeEngine {

    @Override
    public Thread run(final IRobot robot, final String code, final ExecutionListener listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Globals globals = JsePlatform.standardGlobals();
                try {
                    LuaValue luaRobot = CoerceJavaToLua.coerce(robot);
                    globals.set("Robot", luaRobot);
                    globals.load(code).call(luaRobot);
                    listener.onExecutionFinish();
                } catch (LuaError e) {
                    if (e.getCause() instanceof InterruptedException) {
                        listener.onExecutionInterrupted();
                        return;
                    }
                    Gdx.app.error("Lua", e.toString());
                    listener.onExecutionError(e);
                }
            }
        });
        thread.start();
        return thread;
    }
}
