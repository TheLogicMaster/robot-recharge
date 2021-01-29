package com.thelogicmaster.robot_recharge;

import com.thelogicmaster.robot_recharge.code.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LevelSave {
    private boolean usingBlocks;
    private String code, level;
    private Language language;
}
