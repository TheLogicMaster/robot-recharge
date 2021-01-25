package com.thelogicmaster.robot_recharge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LevelInfo {

    private String name;
    private String description;

    @Override
    public String toString() {
        return name;
    }
}
