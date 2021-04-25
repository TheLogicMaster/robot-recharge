package com.thelogicmaster.robot_recharge;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.Array;
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
		// Same process engines
		engines.put(Language.JavaScript, new AndroidJavaScriptEngine());
		engines.put(Language.Lua, new LuaEngine());
		engines.put(Language.Basic, new BasicEngine());
		//engines.put(Language.PHP, new PhpEngine()); // Todo: Did this ever even work for android? If so, fix it, otherwise look into JPHP
		// Separate process engines
		engines.put(Language.Python, new RemoteEngine(this, Language.Python));
		engines.put(Language.Ruby, new RemoteEngine(this, Language.Ruby));

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
		}, new AndroidTTSEngine(getContext()), gameServices, new JavaCodeEditorUtils(), 0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)), config);

		addContentView(editor, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		gameServices.onGpgsActivityResult(requestCode, resultCode, data);
	}
}
