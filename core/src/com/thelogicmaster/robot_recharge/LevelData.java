package com.thelogicmaster.robot_recharge;

public class LevelData {

    private boolean useBlocks;
    private String code, level;
    private Language language;

    public LevelData() {
    }

    public LevelData(String level, boolean useBlocks, String code, Language language) {
        this.useBlocks = useBlocks;
        this.code = code;
        this.language = language;
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public boolean usingBlocks() {
        return useBlocks;
    }

    public void setUseBlocks(boolean useBlocks) {
        this.useBlocks = useBlocks;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
