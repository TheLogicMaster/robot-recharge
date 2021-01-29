package com.thelogicmaster.robot_recharge;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LevelInfo {

    private String name;
    private String description;

    @Override
    public String toString() {
        return name;
    }
}
