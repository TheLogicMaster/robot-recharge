package com.thelogicmaster.robot_recharge.code;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * A code example for a specific level
 */
public class Example {

    private String name;
    private String blocks;
    private String description;
    private String level;
    private ObjectMap<String, String> code;

    public Example() {
        this("", "", "", "", new ObjectMap<String, String>());
    }

    public Example(String name, String blocks, String description, String level, ObjectMap<String, String> code) {
        this.name = name;
        this.blocks = blocks;
        this.description = description;
        this.level = level;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBlocks() {
        return blocks;
    }

    public void setBlocks(String blocks) {
        this.blocks = blocks;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public ObjectMap<String, String> getCode() {
        return code;
    }

    public void setCode(ObjectMap<String, String> code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return name;
    }
}
