package com.thelogicmaster.robot_recharge;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.Nullable;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Clipboard;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.ExecutionListener;
import com.thelogicmaster.robot_recharge.code.IExecutionInstance;
import com.thelogicmaster.robot_recharge.code.Language;

public class RemoteExecutionService extends Service {
	private volatile String error;
	private volatile boolean interrupted;

	@Override
	public void onCreate() {
		// Create mock application to allow Gdx logging to not crash
		Gdx.app = new Application() {
			@Override
			public ApplicationListener getApplicationListener () {
				return null;
			}

			@Override
			public Graphics getGraphics () {
				return null;
			}

			@Override
			public Audio getAudio () {
				return null;
			}

			@Override
			public Input getInput () {
				return null;
			}

			@Override
			public Files getFiles () {
				return null;
			}

			@Override
			public Net getNet () {
				return null;
			}

			@Override
			public void log (String tag, String message) {
			}

			@Override
			public void log (String tag, String message, Throwable exception) {
			}

			@Override
			public void error (String tag, String message) {
			}

			@Override
			public void error (String tag, String message, Throwable exception) {
			}

			@Override
			public void debug (String tag, String message) {
			}

			@Override
			public void debug (String tag, String message, Throwable exception) {
			}

			@Override
			public void setLogLevel (int logLevel) {
			}

			@Override
			public int getLogLevel () {
				return 0;
			}

			@Override
			public void setApplicationLogger (ApplicationLogger applicationLogger) {
			}

			@Override
			public ApplicationLogger getApplicationLogger () {
				return null;
			}

			@Override
			public ApplicationType getType () {
				return null;
			}

			@Override
			public int getVersion () {
				return 0;
			}

			@Override
			public long getJavaHeap () {
				return 0;
			}

			@Override
			public long getNativeHeap () {
				return 0;
			}

			@Override
			public Preferences getPreferences (String name) {
				return null;
			}

			@Override
			public Clipboard getClipboard () {
				return null;
			}

			@Override
			public void postRunnable (Runnable runnable) {
			}

			@Override
			public void exit () {
			}

			@Override
			public void addLifecycleListener (LifecycleListener listener) {
			}

			@Override
			public void removeLifecycleListener (LifecycleListener listener) {
			}
		};
	}

	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}

	@Nullable
	@Override
	public IBinder onBind (Intent intent) {
		Language language = Language.valueOf(intent.getStringExtra("language"));

		CodeEngine engine;
		switch (language) {
		case JavaScript:
			engine = new AndroidJavaScriptEngine();
			break;
		case Lua:
			engine = new LuaEngine();
			break;
		case PHP:
			engine = new PhpEngine();
			break;
		case Ruby:
			engine = new RubyEngine();
			break;
		case Python:
			engine = new AndroidPythonEngine(getApplicationContext());
			break;
		case Basic:
			engine = new BasicEngine();
			break;
		default:
			throw new IllegalStateException("Unsupported Language: " + language);
		}

		engine.initialize();

		return new IRemoteExecutionService.Stub() {
			@Override
			public void run (String code, IRemoteRobot robot) {
				error = null;
				interrupted = false;

				IExecutionInstance executionInstance = engine.run(new IRobot() {
					@Override
					public void move (int distance) {
						try {
							robot.move(distance);
						} catch (RemoteException e) {
							throw new RuntimeException(e);
						}
					}

					@Override
					public void turn (int distance) {
						try {
							robot.turn(distance);
						} catch (RemoteException e) {
							throw new RuntimeException(e);
						}
					}

					@Override
					public void sleep (double duration) {
						try {
							robot.sleep(duration);
						} catch (RemoteException e) {
							throw new RuntimeException(e);
						}
					}

					@Override
					public void speak (String message) {
						try {
							robot.speak(message);
						} catch (RemoteException e) {
							throw new RuntimeException(e);
						}
					}

					@Override
					public void interact () {
						try {
							robot.interact();
						} catch (RemoteException e) {
							throw new RuntimeException(e);
						}
					}
				}, code, new ExecutionListener() {
					@Override
					public void onExecutionFinish () {
					}

					@Override
					public void onExecutionInterrupted () {
						interrupted = true;
					}

					@Override
					public void onExecutionError (String e) {
						if (e.endsWith("Code Interrupted"))
							interrupted = true;
						else
							error = e;
					}
				});

				// Wait for engine execution to finish
				try {
					((ExecutionInstance)executionInstance).getThread().join();
				} catch (InterruptedException ignored){}

				if (error != null)
					throw new IllegalStateException(error);
				if (interrupted)
					throw new IllegalStateException("Code Interrupted");
			}
		};
	}
}
