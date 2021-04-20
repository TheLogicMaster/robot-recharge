package com.thelogicmaster.robot_recharge;

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
					thread.stop();
					Gdx.app.error("ExecutionInstance", "Had to kill thread, this is bad");
				}
			} catch (InterruptedException ignored) {}
		}).start();
	}
}
