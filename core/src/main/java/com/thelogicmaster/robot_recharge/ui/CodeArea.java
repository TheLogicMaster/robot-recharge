package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.thelogicmaster.robot_recharge.RobotRecharge;
import com.thelogicmaster.robot_recharge.code.Language;
import lombok.val;
import lombok.var;


public class CodeArea extends TextArea {
    private final IntArray blockComments = new IntArray();

    private final Language language;

    public CodeArea(Skin skin, Language language) {
        super("", skin, "code");
        this.language = language;
    }

    public void updateLines() {
        calculateOffsets();
    }

    @Override
    protected void drawText(Batch batch, BitmapFont font, float x, float y) {
        float offsetY = -(getStyle().font.getLineHeight() - textHeight) / 2;
        try {
            // Todo: Cache Fields and if possible colorized text
            val firstLineShowingField = ClassReflection.getDeclaredField(TextArea.class, "firstLineShowing");
            firstLineShowingField.setAccessible(true);
            int firstLineShowing = ((int) firstLineShowingField.get(this));
            val linesShowingField = ClassReflection.getDeclaredField(TextArea.class, "linesShowing");
            linesShowingField.setAccessible(true);
            int linesShowing = ((int) linesShowingField.get(this));
            val linesBreakField = ClassReflection.getDeclaredField(TextArea.class, "linesBreak");
            linesBreakField.setAccessible(true);
            val linesBreak = ((IntArray) linesBreakField.get(this));
            for (int i = firstLineShowing * 2; i < (firstLineShowing + linesShowing) * 2 && i < linesBreak.size; i += 2) {
                String line = displayText.subSequence(linesBreak.items[i], linesBreak.items[i + 1]).toString();
                var inBlock = false;
                for (int j = 0; j < blockComments.size; j += 2)
                    if (blockComments.get(j) < linesBreak.items[i] && linesBreak.items[i] < blockComments.get(j + 1) - 1) {
                        inBlock = true;
                        break;
                    }
                val functionPattern = "(^|\\s)((Robot)(" + language.getMemberOperator() + ")(move|turn|speak|sleep|interact))(\\s*\\()";
                val controlPattern = "(?<=^|\\W)(" + language.getKeywords() + ")(?=$|\\W)";
                val commentedFunctions = RobotRecharge.codeEditorUtils.findBlockCommentedMatches(line, linesBreak.items[i], functionPattern, blockComments);
                val commentedControl = RobotRecharge.codeEditorUtils.findBlockCommentedMatches(line, linesBreak.items[i], controlPattern, blockComments);
                if (inBlock)
                    line = "[COMMENT]" + line;
                line = RobotRecharge.codeEditorUtils.colorizeLine(line, commentedFunctions, functionPattern, "$1[FIELD]$3[]$4[FUNCTION]$5[]$6", language.getLineComment());
                line = RobotRecharge.codeEditorUtils.colorizeLine(line, commentedControl, controlPattern, "[KEYWORD]$1[]", language.getLineComment());
                line = line.replaceAll("(" + language.getLineComment() + ")", "[COMMENT]$1");
                line = line.replaceAll("(" + language.getBlockCommentStart() + ")", "[COMMENT]$1");
                line = line.replaceAll("(" + language.getBlockCommentEnd() + ")", "$1[]");
                font.draw(batch, line, x, y + offsetY, 0, Align.left, false);
                offsetY -= font.getLineHeight();
            }
        } catch (ReflectionException e) {
            Gdx.app.error("CodeArea", "Error", e);
        }
    }

    @Override
    protected void calculateOffsets() {
        super.calculateOffsets();
        blockComments.clear();
        RobotRecharge.codeEditorUtils.findBlockComments(text, language.getBlockCommentStart(), language.getBlockCommentEnd(), blockComments);
    }
}
