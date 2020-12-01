package com.thelogicmaster.robot_recharge.desktop;

import com.badlogic.gdx.Gdx;
import com.thelogicmaster.robot_recharge.Consumer;
import com.thelogicmaster.robot_recharge.IBlocklyEditor;
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

public class JCEFBlocklyEditor implements IBlocklyEditor, ActionListener {

    private CefApp cefApp;
    private CefClient client;
    private CefBrowser browser;
    private Consumer<String> blocksCallback, codeCallback;
    private Timer timer;
    private float progress;
    private int width;
    private int x;
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
        progress = 1;
    }

    protected Component getEditor() {
        return browser.getUIComponent();
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void resize(int x, int width, int height) {
        this.x = x;
        this.width = width;
        browser.getUIComponent().setBounds((int) (progress * width) + x, 0, width, height);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if ((shown && progress <= 0) || (!shown && progress >= 1)) {
            timer.stop();
            return;
        }
        progress += shown ? -.25f : .25f;
        browser.getUIComponent().setBounds((int) (progress * width) + x, 0, width, browser.getUIComponent().getHeight());
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
    public void generateCode(Consumer<String> callback) {
        this.codeCallback = callback;
        browser.executeJavaScript("(function() {" +
                "console.log('Code:' + Blockly.JavaScript.workspaceToCode(workspace));" +
                "})();", "", 0);
    }
}
