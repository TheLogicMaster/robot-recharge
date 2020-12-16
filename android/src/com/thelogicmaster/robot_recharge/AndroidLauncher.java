package com.thelogicmaster.robot_recharge;

import android.os.Bundle;
import android.widget.LinearLayout;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.Language;

import java.util.HashMap;

public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        AndroidBlocklyEditor editor = new AndroidBlocklyEditor(getContext());
        HashMap<Language, CodeEngine> engines = new HashMap<>();
        engines.put(Language.JavaScript, new AndroidJavaScriptEngine());
        engines.put(Language.Python, new AndroidPythonEngine(this));
        initialize(new RobotRecharge(engines, editor, new PlatformUtils() {
            @Override
            public void setWindowMode(WindowMode windowMode) {
            }
        }, new AndroidTTSEngine(getContext())), config);
        addContentView(editor, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }
}
