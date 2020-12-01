package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.math.Vector3;

public class Position {

    public int x, y, z;

    public Position() {
    }

    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 toVector(Vector3 vector) {
        return vector.set(x, y, z);
    }

    public Position set(Position position) {
        x = position.x;
        y = position.y;
        z = position.z;
        return this;
    }

    public Position set(Vector3 vector) {
        x = (int)vector.x;
        y = (int)vector.y;
        z = (int)vector.z;
        return this;
    }
}
