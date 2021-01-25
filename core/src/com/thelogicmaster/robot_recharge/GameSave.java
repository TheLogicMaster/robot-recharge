package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.utils.Array;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an entire cloud save
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameSave {
    private Array<LevelSave> levelSaves;
    private int unlockedLevel;
}
