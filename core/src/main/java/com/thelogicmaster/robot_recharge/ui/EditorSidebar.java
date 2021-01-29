package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.thelogicmaster.robot_recharge.RobotUtils;

public class EditorSidebar extends Table {

    public EditorSidebar(Skin skin, final EditorSidebarListener listener) {
        super(skin);
        setBackground("editorSidebar");

        ImageButton closeEditorButton = new ImageButton(skin, "closeEditor");
        add(closeEditorButton).padBottom(128).row();
        ImageButton saveEditorButton = new ImageButton(skin, "saveEditor");
        add(saveEditorButton).padBottom(128).row();
        ImageButton revertEditorButton = new ImageButton(skin, "revertEditor");
        add(revertEditorButton);

        closeEditorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                listener.onClose();
            }
        });
        saveEditorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                listener.onSave();
            }
        });
        revertEditorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RobotUtils.playNavigationSound();
                listener.onRevert();
            }
        });
    }

    public interface EditorSidebarListener {
        void onClose();

        void onRevert();

        void onSave();
    }
}
