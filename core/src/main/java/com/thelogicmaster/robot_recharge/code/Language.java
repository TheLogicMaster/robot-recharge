package com.thelogicmaster.robot_recharge.code;

public enum Language {
    JavaScript("//", "/\\*", "\\*/", "\\.",
            "abstract|break|char|debugger|double|export|finally|goto|in|let|null|public|super|throw|try|" +
                    "volatile|arguments|byte|class|default|else|extends|float|if|instanceof|long|package|return|" +
                    "switch|throws|typeof|while|await|case|const|delete|enum|false|for|implements|int|native|private|" +
                    "short|synchronized|transient|var|with|boolean|catch|continue|do|eval|final|function|import|" +
                    "interface|new|protected|static|this|true|void|yield"),
    Lua("--", "--\\[\\[", "--\\]\\]", ":",
            "and|end|in|repeat|break|false|local|return|do|for|nil|then|else|function|not|true|elseif|if|or|" +
                    "until|while"),
    PHP("//", "/\\*", "\\*/", "->",
            "break|clone|die|empty|endswitch|final|function|include|isset|or|require|throw|var|abstract|" +
                    "callable|const|do|enddeclare|endwhile|finally|global|include_once|list|print|require_once|trait|" +
                    "while|and|case|continue|echo|endfor|eval|fn|goto|instanceof|match|private|return|try|xor|array|" +
                    "catch|declare|else|endforeach|exit|for|if|insteadof|namespace|protected|static|unset|yield|as|" +
                    "class|default|elseif|endif|extends|foreach|implements|interface|new|public|switch|use|from"),
    Python("#", "\"\"\"", "\"\"\"", "\\.",
            "and|as|asset|break|class|continue|def|del|elif|else|except|False|finally|for|from|global|if|" +
                    "import|in|is|lambda|None|nonlocal|not|or|pass|raise|return|True|try|while|with|yield"),
    Basic("'", null, null, null, "goto");

    private final String lineComment, blockCommentStart, blockCommentEnd, memberOperator, keywords;

    Language(String lineComment, String blockCommentStart, String blockCommentEnd, String memberOperator, String keywords) {
        this.lineComment = lineComment;
        this.blockCommentStart = blockCommentStart;
        this.blockCommentEnd = blockCommentEnd;
        this.memberOperator = memberOperator;
        this.keywords = keywords;
    }

    public String getLineComment() {
        return lineComment;
    }

    public String getBlockCommentStart() {
        return blockCommentStart;
    }

    public String getBlockCommentEnd() {
        return blockCommentEnd;
    }

    public String getMemberOperator() {
        return memberOperator;
    }

    public String getKeywords() {
        return keywords;
    }
}
