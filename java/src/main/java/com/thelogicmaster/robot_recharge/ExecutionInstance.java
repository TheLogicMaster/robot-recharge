package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.thelogicmaster.robot_recharge.code.IExecutionInstance;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExecutionInstance implements IExecutionInstance {

	private final Thread thread;

	public void stop () {
		if (!thread.isAlive())
			return;
		thread.interrupt();

		// Kill thread if in an infinite loop
		new Thread(() -> {
			try {
				Thread.sleep(100);
				if (thread.isAlive()) {
					if (Gdx.app.getType() != Application.ApplicationType.Android)
						thread.stop();
					Gdx.app.error("ExecutionInstance", "Failed to interrupt thread, this is real bad");
				}
			} catch (InterruptedException ignored) {}
		}).start();
	}
}
