package com.thelogicmaster.robot_recharge;

import android.os.Bundle;

import android.widget.LinearLayout;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {

	boolean initialized;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		AndroidBlocklyEditor editor = new AndroidBlocklyEditor(getContext());
		initialize(new RobotGame(new AndroidJavaScriptEngine(), editor), config);
		addContentView(editor, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
	}
}
