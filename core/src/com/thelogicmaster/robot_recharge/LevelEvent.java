package com.thelogicmaster.robot_recharge;

import com.thelogicmaster.robot_recharge.blocks.Block;

public class LevelEvent {

    private transient Block source;
    private String id;
    private String event;

    public LevelEvent() {
    }

    public LevelEvent(Block source, String id, String event) {
        this.source = source;
        this.id = id;
        this.event = event;
    }

    public Block getSource() {
        return source;
    }

    public void setSource(Block source) {
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return id + ":" + event;
    }
}
