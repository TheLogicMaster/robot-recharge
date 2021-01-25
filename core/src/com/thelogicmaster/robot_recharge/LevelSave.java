package com.thelogicmaster.robot_recharge;

import com.thelogicmaster.robot_recharge.code.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LevelSave {
    private boolean usingBlocks;
    private String code, level;
    private Language language;
}
