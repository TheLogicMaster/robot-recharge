package com.thelogicmaster.robot_recharge;

public class LevelInfo {

    String name;
    String description;

    public LevelInfo(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public LevelInfo() {
        this("", "");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
