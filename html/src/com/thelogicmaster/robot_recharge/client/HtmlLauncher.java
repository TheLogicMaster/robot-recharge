package com.thelogicmaster.robot_recharge.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.GwtGraphics;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.FreetypeInjector;
import com.badlogic.gdx.utils.Array;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Panel;
import com.thelogicmaster.robot_recharge.*;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.Language;

import java.util.HashMap;

@SuppressWarnings("Convert2MethodRef")
public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig() {
        GwtApplicationConfiguration config = new GwtApplicationConfiguration(true);
        config.padHorizontal = 0;
        config.padVertical = 0;
        return config;
    }

    private native boolean ttsSupported()/*-{
        return 'speechSynthesis' in $wnd;
    }-*/;

    @Override
    public ApplicationListener createApplicationListener() {
        final GwtBlocklyEditor editor = new GwtBlocklyEditor();

        HashMap<Language, CodeEngine> engines = new HashMap<>();
        engines.put(Language.JavaScript, null);

        final GwtGameServices gwtGameServices = new GwtGameServices();

        final Array<WindowMode> windowModes = new Array<>(new WindowMode[]{
                WindowMode.Windowed,
                WindowMode.Fullscreen
        });

        return new RobotRecharge(engines, editor, new PlatformUtils() {
            @Override
            public void setWindowMode(WindowMode windowMode) {
                if (windowMode == WindowMode.Windowed)
                    Gdx.graphics.setWindowedMode(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                else
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }

            @Override
            public Array<WindowMode> getWindowModes() {
                return windowModes;
            }

            @Override
            public RobotController createRobotController(Robot robot, RobotExecutionListener listener, CodeEngine engine) {
                return new JavaScriptRobotController(robot, listener);
            }
        }, ttsSupported() ? new GwtTTSEngine() : null, gwtGameServices, new HtmlCodeEditorUtils(), BuildConfig.DEBUG);
    }

    @Override
    public void onModuleLoad() {
        FreetypeInjector.inject(() -> HtmlLauncher.super.onModuleLoad());
    }

    /*@Override
    public Preloader.PreloaderCallback getPreloaderCallback() {
        return createPreloaderPanel(GWT.getHostPageBaseURL() + "preload.png");
    }*/

    @Override
    protected void adjustMeterPanel(Panel meterPanel, Style meterStyle) {
        meterPanel.setStyleName("gdx-meter");
        meterStyle.setProperty("backgroundColor", "#00ffff");
        meterStyle.setProperty("backgroundImage", "none");
    }
}