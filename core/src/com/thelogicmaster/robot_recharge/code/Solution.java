package com.thelogicmaster.robot_recharge.code;

import com.badlogic.gdx.utils.ObjectMap;

public class Solution {

    private ObjectMap<String, String> code;
    private String blocks;
    private String name;
    private String description;

    public ObjectMap<String, String> getCode() {
        return code;
    }

    public void setCode(ObjectMap<String, String> code) {
        this.code = code;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name == null ? "Solution" : name;
    }
}
