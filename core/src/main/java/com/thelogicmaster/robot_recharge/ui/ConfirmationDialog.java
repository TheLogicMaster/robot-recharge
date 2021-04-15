package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.thelogicmaster.robot_recharge.RobotUtils;

public class ConfirmationDialog extends RobotDialog {

	private boolean instantClose;

	public ConfirmationDialog (String title, String text, ConfirmationListener listener, boolean instantClose) {
		super(title);

		this.instantClose = instantClose;

		add(new Label(text, getSkin())).row();

		Table buttonTable = new Table(getSkin());
		TextButton cancelButton = new PaddedTextButton("Cancel", getSkin());
		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				RobotUtils.playNavigationSound();
				hide();
				listener.onCancel();
			}
		});
		buttonTable.add(cancelButton).expandX().left();

		TextButton okayButton = new PaddedTextButton("Okay", getSkin());
		okayButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				RobotUtils.playNavigationSound();
				hide();
				listener.onConfirm();
			}
		});
		buttonTable.add(okayButton).expandX();

		add(buttonTable).bottom().expand().fillX();
	}

	public ConfirmationDialog (String title, String text, ConfirmationListener listener) {
		this(title, text, listener, false);
	}

	@Override
	public void hide () {
		if (instantClose)
			setVisible(false);
		else
			super.hide();
	}

	public interface ConfirmationListener {

		void onConfirm();

		void onCancel();
	}

	public static class ConfirmationAdaptor implements ConfirmationListener {
		@Override
		public void onConfirm () {
		}

		@Override
		public void onCancel () {
		}
	}
}
