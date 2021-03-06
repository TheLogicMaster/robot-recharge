package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.thelogicmaster.robot_recharge.code.Language;

public class CodeEditor extends Table {

    private final CodeArea codeArea;

    public CodeEditor(Skin skin, Language language, ChangeListener onCatalogButton) {
        super(skin);
        setBackground("windowTen");
        final ImageButton catalogButton = new ImageButton(skin, "programmingCatalog");
        add(catalogButton).padLeft(10).padTop(10).padRight(100).left();
        add(new Label(language.name() + " Editor", skin, "large")).left().expandX().row();
        final Table codeTable = new Table();
        final TextArea lineNumbers = new TextArea("", skin, "codeNumbers");
        lineNumbers.setDisabled(true);
        codeTable.add(lineNumbers).width(80).fillY();
        codeArea = new CodeArea(skin, language);
        codeArea.setProgrammaticChangeEvents(true);
        codeTable.add(codeArea).grow();
        final ScrollPane codeScrollPane = new ScrollPane(codeTable, skin);
        codeScrollPane.setForceScroll(false, true);
        codeScrollPane.setOverscroll(false, false);
        codeScrollPane.setFadeScrollBars(false);
        codeScrollPane.setFlickScroll(false);
        add(codeScrollPane).pad(10, 10, 10, 10).colspan(2).grow();
        setVisible(false);
        codeArea.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.postRunnable(() -> {
                    // Todo: Fix initial line count
                    codeArea.updateLines();
                    codeArea.setPrefRows(codeArea.getLines() + 1);
                    codeArea.invalidateHierarchy();
                    if (Math.abs(-codeScrollPane.getHeight() / 2 - codeArea.getCursorY() - codeScrollPane.getScrollY()) > codeScrollPane.getHeight() / 2)
                        codeScrollPane.setScrollY(-codeScrollPane.getHeight() / 2 - codeArea.getCursorY());
                    StringBuilder numbers = new StringBuilder();
                    for (int i = 1; i <= codeArea.getLines(); i++)
                        numbers.append(i).append("\n");
                    lineNumbers.setText(numbers.toString());
                });
            }
        });
        catalogButton.addListener(onCatalogButton);
    }

    public void setCode(String code) {
        codeArea.setText(code);
    }

    public String getCode() {
        return codeArea.getText();
    }
}
