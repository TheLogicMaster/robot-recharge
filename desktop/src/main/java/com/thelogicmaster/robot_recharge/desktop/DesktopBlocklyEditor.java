package com.thelogicmaster.robot_recharge.desktop;

import com.badlogic.gdx.Gdx;
import com.thelogicmaster.robot_recharge.Consumer;
import com.thelogicmaster.robot_recharge.code.BlocklyEditor;
import com.thelogicmaster.robot_recharge.code.Language;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.cef.handler.CefLoadHandlerAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DesktopBlocklyEditor implements BlocklyEditor, ActionListener {

    private CefApp cefApp;
    private CefClient client;
    private CefBrowser browser;
    private Consumer<String> blocksCallback, codeCallback;
    private Timer timer;
    private float progress;
    private int screenWidth;
    private int width;
    private boolean shown, loaded;

    public void setup() {
        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = true;
        cefApp = CefApp.getInstance(settings);
        client = cefApp.createClient();

        String index = getClass().getResource("/blockly/webview.html").toString();
        if ("jar".equals(getClass().getResource("").getProtocol()))
            try {
                File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                JarFile jar = new JarFile(file);
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (!entry.toString().startsWith("blockly/"))
                        continue;
                    if (entry.isDirectory()) {
                        new File(file.getParent() + File.separator + entry.toString()).mkdir();
                        continue;
                    }
                    File outputFile = new File(file.getParent() + File.separator + entry.toString());
                    if (entry.getSize() == outputFile.length())
                        continue;
                    try (InputStream input = jar.getInputStream(entry); OutputStream output = new FileOutputStream(outputFile)) {
                        while (input.available() > 0)
                            output.write(input.read());
                    }
                }
                index = "file:" + file.getParent() + "/blockly/webview.html";
            } catch (IOException | URISyntaxException e) {
                Gdx.app.error("Blockly", "Failed to initialize blockly", e);
            }

        browser = client.createBrowser(index, true, false);
        client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
                if (message.startsWith("Code:")) {
                    if (codeCallback == null)
                        return false;
                    codeCallback.accept(message.substring(5));
                    codeCallback = null;
                } else if (message.startsWith("Blocks:")) {
                    if (blocksCallback == null)
                        return false;
                    blocksCallback.accept(message.substring(7));
                    blocksCallback = null;
                } else
                    return false;
                return true;
            }
        });
        client.addLoadHandler(new CefLoadHandlerAdapter() {
            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                loaded = true;
            }
        });
        browser.createImmediately();
        timer = new Timer(0, this);
    }

    protected Component getEditor() {
        return browser.getUIComponent();
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
        browser.getUIComponent().setBounds(screenWidth - (int) (progress * width), 0, width, Gdx.graphics.getHeight());
    }

    @Override
    public void resize(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        browser.getUIComponent().setBounds(screenWidth - (int) (progress * width), 0, width, screenHeight);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if ((!shown && progress <= 0) || (shown && progress >= 1)) {
            timer.stop();
            return;
        }
        progress += shown ? .25f : -.25f;
        browser.getUIComponent().setLocation(screenWidth - (int) (progress * width), 0);
    }

    @Override
    public boolean isShown() {
        return shown;
    }

    @Override
    public void show() {
        shown = true;
        timer.start();
    }

    @Override
    public void hide() {
        shown = false;
        timer.start();
    }

    @Override
    public void load(String data) {
        browser.executeJavaScript(
                "workspace.clear();" +
                        "Blockly.Xml.domToWorkspace(Blockly.Xml.textToDom('" + data + "'), workspace);", "", 0);
    }

    @Override
    public void clear() {
        browser.executeJavaScript(
                "workspace.clear();", "", 0);
    }

    @Override
    public void save(Consumer<String> callback) {
        this.blocksCallback = callback;
        browser.executeJavaScript("(function() {" +
                "console.log('Blocks:' + Blockly.Xml.domToText(Blockly.Xml.workspaceToDom(workspace)));" +
                "})();", "", 0);
    }

    @Override
    public void generateCode(Language language, Consumer<String> callback) {
        this.codeCallback = callback;
        browser.executeJavaScript("(function() {" +
                "console.log('Code:' + Blockly." + language.name() + ".workspaceToCode(workspace));" +
                "})();", "", 0);
    }

    @Override
    public void setTheme(String theme) {
        browser.executeJavaScript("" +
                "let theme = JSON.parse(`" + theme + "`);\n" +
                "theme['base'] = Blockly.Themes.Dark;\n" +
                "workspace.setTheme(Blockly.Theme.defineTheme('theme', theme));\n", "", 0);
    }
}
