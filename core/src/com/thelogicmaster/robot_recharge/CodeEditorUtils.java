package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.utils.IntArray;

public interface CodeEditorUtils {

    // Todo: Refactor names to something logical
    boolean[] findBlockCommentedMatches(String line, int pos, String pattern, IntArray blockComments);

    String colorizeLine(String line, boolean[] commented, String pattern, String replacement, String lineComment);

    void findBlockComments(String text, String commentStart, String commentEnd, IntArray blockComments);
}
