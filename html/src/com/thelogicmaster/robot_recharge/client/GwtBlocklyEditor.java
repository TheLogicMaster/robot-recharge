package com.thelogicmaster.robot_recharge.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtGraphics;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.thelogicmaster.robot_recharge.Consumer;
import com.thelogicmaster.robot_recharge.code.BlocklyEditor;
import com.thelogicmaster.robot_recharge.code.Language;

public class GwtBlocklyEditor implements BlocklyEditor {

    private boolean shown;
    private final DivElement blockly, game;
    private int width, screenWidth, screenHeight;

    public GwtBlocklyEditor() {
        blockly = (DivElement) Document.get().getElementById("blocklyDiv");
        game = (DivElement) Document.get().getElementById("embed-html");
        updateStyle();
    }

    private native void resizeBlockly()/*-{
        $wnd.Blockly.svgResize($wnd.workspace);
    }-*/;

    private native void clearBlockly()/*-{
        $wnd.workspace.clear();
    }-*/;

    private native void loadBlockly(String data)/*-{
        $wnd.Blockly.Xml.domToWorkspace($wnd.Blockly.Xml.textToDom(data), $wnd.workspace);
    }-*/;

    private native String saveBlockly()/*-{
        return $wnd.Blockly.Xml.domToText($wnd.Blockly.Xml.workspaceToDom($wnd.workspace));
    }-*/;

    private native String generateBlockly(String language)/*-{
        return $wnd.Blockly[language].workspaceToCode($wnd.workspace);
    }-*/;

    private native void setThemeBlockly(String theme)/*-{
        $wnd.workspace.setTheme(JSON.parse(theme));
    }-*/;

    void init() {

    }

    private void updateStyle() {
        blockly.setAttribute("style", "" +
                "position: absolute; " +
                "left: " + (screenWidth - width - 10) + "px; " +
                "width: " + (width + 10) + "px; " +
                "height: " + (screenHeight - 4) + "px; " +
                (shown ? "" : "display: none;")
        );
        resizeBlockly();
    }

    @Override
    public void show() {
        shown = true;
        updateStyle();
    }

    @Override
    public void resize(int screenWidth, int screenHeight) {
        this.screenWidth = game.getPropertyInt("clientWidth");
        this.screenHeight = game.getPropertyInt("clientHeight");
        updateStyle();
    }

    @Override
    public void setWidth(int width) {
        this.width = (int) (width / GwtGraphics.getNativeScreenDensity());
        updateStyle();
    }

    @Override
    public void hide() {
        shown = false;
        updateStyle();
    }

    @Override
    public boolean isShown() {
        return shown;
    }

    @Override
    public void load(String data) {
        clearBlockly();
        try {
            loadBlockly(data);
        } catch (Exception e) {
            Gdx.app.error("Blockly", "Error loading", e);
        }
    }

    @Override
    public void clear() {
        clearBlockly();
    }

    @Override
    public void save(Consumer<String> callback) {
        callback.accept(saveBlockly());
    }

    @Override
    public void generateCode(Language language, Consumer<String> callback) {
        callback.accept(generateBlockly(language.name()));
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void setTheme(String theme) {
        setThemeBlockly(theme);
    }
}
