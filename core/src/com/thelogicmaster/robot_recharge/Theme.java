package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.graphics.Color;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Theme {
    private Color function, field, keyword, comment;
    private Color code, cursor, text;
    private Color primary, accent, secondary;
}
