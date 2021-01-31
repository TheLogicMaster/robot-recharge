package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.BooleanArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.thelogicmaster.robot_recharge.RobotRecharge;
import com.thelogicmaster.robot_recharge.code.Language;
import lombok.val;
import lombok.var;
import regexodus.*;

import java.util.concurrent.atomic.AtomicInteger;

import static regexodus.Replacer.wrap;

public class CodeArea extends TextArea {
    private final IntArray blockComments = new IntArray();

    private final Language language;
    private final Field firstLineShowingField, linesShowingField, linesBreakField;

    public CodeArea(Skin skin, Language language) {
        super("", skin, "code");
        this.language = language;
        try {
            firstLineShowingField = ClassReflection.getDeclaredField(TextArea.class, "firstLineShowing");
            linesShowingField = ClassReflection.getDeclaredField(TextArea.class, "linesShowing");
            linesBreakField = ClassReflection.getDeclaredField(TextArea.class, "linesBreak");
        } catch (ReflectionException e) {
            throw new RuntimeException("Couldn't get TextArea field", e);
        }
    }

    public void updateLines() {
        calculateOffsets();
    }

    public boolean[] findBlockCommentedMatches(String line, int pos, String pattern, IntArray blockComments) {
        val matcher = Pattern.compile(pattern).matcher(line);
        BooleanArray commented = new BooleanArray(32);
        while (matcher.find()) {
            boolean isCommented = false;
            for (int j = 0; j < blockComments.size; j += 2)
                if (blockComments.get(j) <= matcher.start() + pos && matcher.end() + pos <= blockComments.get(j + 1)) {
                    isCommented = true;
                    break;
                }
            commented.add(isCommented);
        }
        commented.shrink();
        return commented.items;
    }

    public String colorizeLine(String line, boolean[] commented, String pattern, String replacement, String lineComment) {
        val compiled = Pattern.compile(pattern);
        val matcher = compiled.matcher(line);
        Replacer replacer = new Replacer(compiled, replacement);
        TextBuffer buffer = wrap(new StringBuffer());
        AtomicInteger i = new AtomicInteger();
        Replacer.replace(matcher, (match, dest) ->{
                Gdx.app.log("" + i, "" + match.start() + " " + match.group() + " " + match.end());
        Gdx.app.log("line", line.substring(0, match.start()));
        Gdx.app.log("line", line.substring(match.start(), match.end()));
        if (!commented[i.get()] && !line.substring(0, match.start()).matches(".*?" + lineComment + ".*?"))
            replacer.replace(match.group(), dest);
        else
            match.getGroup(0, dest);
        i.getAndIncrement();
        }, buffer);

        Gdx.app.log("line", line);
        /*return matcher.replaceAll((match, dest) -> {
            Gdx.app.log("" + i, "" + match.start() + " " + match.group() + " " + match.end());
            Gdx.app.log("line", line.substring(0, match.start()));
            Gdx.app.log("line", line.substring(match.start(), match.end()));
            if (!commented[i.get()] && !line.substring(0, match.start()).matches(".*?" + lineComment + ".*?"))
                replacer.replace(match.group(), dest);
            else
                match.getGroup(0, dest);
            i.getAndIncrement();
        });*/
        Gdx.app.log("out", buffer.toString());
        return buffer.toString();
    }

    public void findBlockComments(String text, String commentStart, String commentEnd, IntArray blockComments) {
        val matcher = Pattern.compile(commentStart + "(\\s|\\S)*?" + commentEnd).matcher(text);
        while (matcher.find()) {
            blockComments.add(matcher.start());
            blockComments.add(matcher.end());
        }
    }

    @Override
    protected void drawText(Batch batch, BitmapFont font, float x, float y) {
        float offsetY = -(getStyle().font.getLineHeight() - textHeight) / 2;
        try {
            // Todo: Cache colorized text if possible
            firstLineShowingField.setAccessible(true);
            int firstLineShowing = ((int) firstLineShowingField.get(this));
            linesShowingField.setAccessible(true);
            int linesShowing = ((int) linesShowingField.get(this));
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
                // Todo: Somehow get RegExodus working
                // Todo: Maybe treat Robot as a differently colored keyword and use a positive lookbehind for the functions?
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
