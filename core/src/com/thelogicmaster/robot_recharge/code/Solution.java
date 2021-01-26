package com.thelogicmaster.robot_recharge.code;

import com.badlogic.gdx.utils.ObjectMap;
import lombok.*;

@Getter
@Setter
public class Solution {

    private ObjectMap<String, String> code;
    private String blocks;
    private String name;
    private String description;

    @Override
    public String toString() {
        return name == null ? "Solution" : name;
    }
}
