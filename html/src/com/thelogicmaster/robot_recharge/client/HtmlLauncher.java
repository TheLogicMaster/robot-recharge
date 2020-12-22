package com.thelogicmaster.robot_recharge.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.FreetypeInjector;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.inject.OnCompletion;
import com.google.gwt.core.client.ScriptInjector;
import com.thelogicmaster.robot_recharge.*;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.Language;

import java.util.HashMap;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig() {
        return new GwtApplicationConfiguration(true);
    }

    private native boolean ttsSupported()/*-{
        return 'speechSynthesis' in $wnd;
    }-*/;

    @Override
    public ApplicationListener createApplicationListener() {
        final GwtBlocklyEditor editor = new GwtBlocklyEditor();
        HashMap<Language, CodeEngine> engines = new HashMap<>();
        engines.put(Language.JavaScript, null);
        return new RobotRecharge(engines, editor, new PlatformUtils() {
            @Override
            public void setWindowMode(WindowMode windowMode) {

            }

            @Override
            public RobotController createRobot(Robot controller, RobotExecutionListener listener, CodeEngine engine) {
                return new JavaScriptRobotController(controller, listener);
            }
        }, ttsSupported() ? new GwtTTSEngine() : null) {
            @Override
            public void create() {
                super.create();
                editor.init();
            }
        };
    }

    @Override
    public void onModuleLoad() {
        FreetypeInjector.inject(new OnCompletion() {
            public void run() {
                HtmlLauncher.super.onModuleLoad();
            }
        });
    }
}