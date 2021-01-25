package com.thelogicmaster.robot_recharge.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.BooleanArray;
import com.badlogic.gdx.utils.IntArray;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.thelogicmaster.robot_recharge.CodeEditorUtils;

public class HtmlCodeEditorUtils implements CodeEditorUtils {

    @Override
    public boolean[] findBlockCommentedMatches(String line, int pos, String pattern, IntArray blockComments) {
        RegExp re = RegExp.compile(pattern, "g");
        MatchResult match;
        BooleanArray commented = new BooleanArray(32);
        while ((match = re.exec(line)) != null) {
            boolean isCommented = false;
            for (int j = 0; j < blockComments.size; j += 2) // Todo: Ensure length - 1 is right
                if (blockComments.get(j) <= match.getIndex() + pos && match.getIndex() + match.getGroup(0).length() - 1 + pos <= blockComments.get(j + 1)) {
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
        RegExp re = RegExp.compile(pattern);
        MatchResult match = re.exec(line);
        if (match != null) {
            int i = 0;
            StringBuilder buffer = new StringBuilder();
            String remaining = line;
            do {
                if (i >= commented.length) {
                    Gdx.app.log("Code Regex Error", "Inconsistent match count");
                    break;
                }
                buffer.append(remaining, 0, match.getIndex());
                remaining = remaining.substring(match.getIndex() + match.getGroup(0).length());
                if (!commented[i] && !buffer.toString().matches(".*?" + lineComment + ".*?"))
                    buffer.append(re.replace(match.getGroup(0), replacement));
                else
                    buffer.append(match.getGroup(0));
                i++;
            } while ((match = re.exec(remaining)) != null);
            buffer.append(remaining);
            return buffer.toString();
        } else
            return line;
    }

    @Override
    public void findBlockComments(String text, String commentStart, String commentEnd, IntArray blockComments) {
        RegExp re = RegExp.compile(commentStart + "(\\s|\\S)*?" + commentEnd, "g");
        MatchResult match;
        while ((match = re.exec(text)) != null) {
            blockComments.add(match.getIndex());
            blockComments.add(match.getIndex() + match.getGroup(0).length());
        }
    }
}
