package com.thelogicmaster.robot_recharge;

import com.thelogicmaster.robot_recharge.blocks.Block;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LevelEvent {
    private transient Block source;
    private String id;
    private String event;

    public LevelEvent(String id, String event) {
        this.id = id;
        this.event = event;
    }

    @Override
    public String toString() {
        return id + ":" + event;
    }
}
