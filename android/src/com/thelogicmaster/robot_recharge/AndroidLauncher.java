package com.thelogicmaster.robot_recharge;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.Array;
import com.google.android.gms.common.GoogleApiAvailability;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.Language;

import java.util.HashMap;

public class AndroidLauncher extends AndroidApplication {

    private AndroidGameServices gameServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true;
        AndroidBlocklyEditor editor = new AndroidBlocklyEditor(getContext());
        HashMap<Language, CodeEngine> engines = new HashMap<>();
        engines.put(Language.JavaScript, new AndroidJavaScriptEngine());
        engines.put(Language.Python, new AndroidPythonEngine(this));
        engines.put(Language.Lua, new LuaEngine());
        engines.put(Language.PHP, new PhpEngine());
        gameServices = new AndroidGameServices();
        gameServices.initialize(this, true);
        final Array<WindowMode> windowModes = new Array<>();
        initialize(new RobotRecharge(engines, editor, new PlatformUtils() {
            @Override
            public void setWindowMode(final WindowMode windowMode) {
            }

            @Override
            public Array<WindowMode> getWindowModes() {
                return windowModes;
            }

            @Override
            public RobotController createRobotController(Robot robot, RobotExecutionListener listener, CodeEngine engine) {
                return new JavaRobotController(robot, listener, engine);
            }
        }, new AndroidTTSEngine(getContext()), gameServices, 0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)), config);

        addContentView(editor, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gameServices.onGpgsActivityResult(requestCode, resultCode, data);
    }
}
