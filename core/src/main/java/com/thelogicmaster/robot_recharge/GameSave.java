package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.utils.Array;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an entire cloud save
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameSave {
    private Array<LevelSave> levelSaves;
    private int unlockedLevel;
}
