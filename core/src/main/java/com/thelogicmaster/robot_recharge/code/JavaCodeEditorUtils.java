package com.thelogicmaster.robot_recharge.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.BooleanArray;
import com.badlogic.gdx.utils.IntArray;
import com.thelogicmaster.robot_recharge.CodeEditorUtils;
import lombok.val;

import java.util.regex.Pattern;

public class JavaCodeEditorUtils implements CodeEditorUtils {

    @Override
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

    @Override
    public String colorizeLine(String line, boolean[] commented, String pattern, String replacement, String lineComment) {
        val matcher = Pattern.compile(pattern).matcher(line);
        val buffer = new StringBuffer();
        int i = 0;
        while (matcher.find()) {
            if (i >= commented.length) {
                Gdx.app.error("Code Regex", "Inconsistent match count");
                break;
            }
            if (!commented[i] && !line.substring(0, matcher.start()).matches(".*?" + lineComment + ".*?"))
                matcher.appendReplacement(buffer, replacement);
            i++;
        }
        return matcher.appendTail(buffer).toString();
    }

    @Override
    public void findBlockComments(String text, String commentStart, String commentEnd, IntArray blockComments) {
        val matcher = Pattern.compile(commentStart + "(\\s|\\S)*?" + commentEnd).matcher(text);
        while (matcher.find()) {
            blockComments.add(matcher.start());
            blockComments.add(matcher.end());
        }
    }
}
