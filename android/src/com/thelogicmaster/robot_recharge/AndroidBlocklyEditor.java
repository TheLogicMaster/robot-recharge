package com.thelogicmaster.robot_recharge;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import com.badlogic.gdx.Gdx;
import com.thelogicmaster.robot_recharge.code.BlocklyEditor;
import com.thelogicmaster.robot_recharge.code.Language;
import org.apache.commons.text.StringEscapeUtils;

public class AndroidBlocklyEditor extends WebView implements BlocklyEditor {

    private boolean loaded;
    private int width;

    @SuppressLint("SetJavaScriptEnabled")
    public AndroidBlocklyEditor(Context context) {
        super(context);
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loaded = true;
                super.onPageFinished(view, url);
            }
        });
        getSettings().setJavaScriptEnabled(true);
        setWebChromeClient(new WebChromeClient());
        loadUrl("file:///android_asset/blockly/webview.html");
        setVisibility(GONE);
    }

    @Override
    public void show() {
        ((Activity) getContext()).runOnUiThread(() -> setVisibility(VISIBLE));
    }

    private void updateLayout() {
        ((Activity) getContext()).runOnUiThread(() -> {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
            params.leftMargin = Gdx.graphics.getWidth() - width;
            setLayoutParams(params);
        });
    }

    @Override
    public void setWidth(final int width) {
        this.width = width;
        updateLayout();
    }

    @Override
    public void resize(final int screenWidth, final int screenHeight) {
        updateLayout();
    }

    @Override
    public void hide() {
        ((Activity) getContext()).runOnUiThread(() -> setVisibility(INVISIBLE));
    }

    @Override
    public boolean isShown() {
        return getVisibility() == VISIBLE;
    }

    private void runJavascript(final String code, final ValueCallback<String> callback) {
        ((Activity) getContext()).runOnUiThread(() -> evaluateJavascript(code, callback));
    }

    @Override
    public void load(final String data) {
        runJavascript(
                "workspace.clear();" +
                        "Blockly.Xml.domToWorkspace(Blockly.Xml.textToDom('" + data + "'), workspace);",
                null);
    }

    @Override
    public void clear() {
        runJavascript("workspace.clear();", null);
    }

    @Override
    public void save(final Consumer<String> callback) {
        runJavascript("(function() {" +
                "return Blockly.Xml.domToText(Blockly.Xml.workspaceToDom(workspace));" +
                "})();", value -> {
            value = value.substring(1, value.length() - 1);
            callback.accept(value);
        });
    }

    @Override
    public void generateCode(Language language, final Consumer<String> callback) {
        runJavascript("(function() {" +
                "return Blockly." + language.name() + ".workspaceToCode(workspace);" +
                "})();", value -> {
            if (value.length() > 2)
                value = value.substring(1, value.length() - 2);
            callback.accept(StringEscapeUtils.unescapeJson(value));
        });
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void setTheme(String theme) {
        runJavascript("" +
                "var theme = JSON.parse('" + theme + "');\n" +
                "theme['base'] = Blockly.Themes.Dark;\n" +
                "workspace.setTheme(theme);\n", null);
    }
}
