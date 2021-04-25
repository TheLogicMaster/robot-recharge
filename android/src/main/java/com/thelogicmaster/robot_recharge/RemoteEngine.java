package com.thelogicmaster.robot_recharge;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.Process;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.ExecutionListener;
import com.thelogicmaster.robot_recharge.code.IExecutionInstance;
import com.thelogicmaster.robot_recharge.code.Language;
import com.thelogicmaster.robot_recharge.code.java.ExecutionInstance;

public class RemoteEngine implements CodeEngine, Disposable {
	private final Activity activity;
	private final ServiceConnection connection;
	private final Object lock = new Object();
	private final Intent intent;
	private volatile IRemoteExecutionService service;
	private volatile Thread currentThread;
	private volatile boolean running;

	public RemoteEngine (Activity activity, Language language) {
		this.activity = activity;

		connection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName className, IBinder binder) {
				Gdx.app.debug("RemoteEngine", "Execution service connected");
				service = IRemoteExecutionService.Stub.asInterface(binder);
				synchronized (lock) {
					lock.notifyAll();
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName className) {
				Gdx.app.debug("RemoteEngine", "Execution service disconnected");
				service = null;
			}
		};

		intent = new Intent(activity, RemoteExecutionService.class)
			.setAction(IRemoteExecutionService.class.getName())
			.putExtra("language", language.name());
	}

	private <T> T wrapCall(RobotCall<T> call) {
		currentThread = Thread.currentThread();
		try {
			return call.call();
		} catch (InterruptedException e) {
			throw new IllegalStateException("Code Interrupted");
		} finally {
			currentThread = null;
		}
	}

	@Override
	public void initialize () {
		activity.startService(intent);
		activity.bindService(intent, connection, 0);
	}

	@Override
	public IExecutionInstance run (IRobot robot, String code, ExecutionListener listener) {
		Thread thread = new Thread(() -> {
			if (running) {
				listener.onExecutionInterrupted();
				return;
			}

			if (service == null) {
				activity.startService(intent);
				activity.bindService(intent, connection, 0);
			}

			// Wait for service binding
			if (service == null)
				try {
					synchronized (lock) {
						lock.wait(5000);
					}
				} catch (InterruptedException e){
					listener.onExecutionInterrupted();
					return;
				}
			if (service == null) {
				listener.onExecutionError("Code engine timed out");
				return;
			}

			running = true;
			try {
				service.run(code, new IRemoteRobot.Stub() {
					@Override
					public void move (int distance) {
						wrapCall(() -> {
							robot.move(distance);
							return null;
						});
					}

					@Override
					public void turn (int distance) {
						wrapCall(() -> {
							robot.turn(distance);
							return null;
						});
					}

					@Override
					public void sleep (double duration) {
						wrapCall(() -> {
							robot.sleep(duration);
							return null;
						});
					}

					@Override
					public void speak (String message) {
						robot.speak(message);
					}

					@Override
					public void interact () {
						robot.interact();
					}
				});
				listener.onExecutionFinish();
			} catch (DeadObjectException e) {
				if (!running)
					return;
				Gdx.app.error("RemoteExecution", "Process died", e);
				listener.onExecutionError("Code engine process died");
			} catch (Exception e) {
				if ("Code Interrupted".equals(e.getMessage())) {
					listener.onExecutionInterrupted();
					return;
				}
				Gdx.app.error("RemoteExecution", "Error", e);
				listener.onExecutionError(e.getMessage());
			} finally {
				running = false;
			}
		});
		thread.start();
		return new ExecutionInstance(thread) {
			@Override
			public void stop () {
				thread.interrupt();
				if (currentThread != null)
					currentThread.interrupt();
				if (running)
					new Thread(() -> {
						try {
							Thread.sleep(200);
							if (running) {
								running = false;
								Gdx.app.log("RemoteEngine", "Killing remote execution process");
								killService();
								listener.onExecutionInterrupted();
							}
						} catch (InterruptedException ignored){}
					}).start();
			}
		};
	}

	private void killService() {
		try {
			activity.unbindService(connection);
		} catch (IllegalArgumentException ignore){}

		activity.stopService(intent);

		ActivityManager manager = (ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo info: manager.getRunningAppProcesses())
			if (info.processName.endsWith(":execution_process")) {
				Process.killProcess(info.pid);
				break;
			}

		service = null;
	}

	@Override
	public void dispose () {
		killService();
	}

	private interface RobotCall<T> {
		T call() throws InterruptedException;
	}
}
