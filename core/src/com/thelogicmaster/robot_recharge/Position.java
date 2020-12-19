package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.math.Vector3;

public class Position {

    public int x, y, z;

    public Position() {
    }

    public Position(Position position) {
        this(position.x, position.y, position.z);
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
        x = (int) vector.x;
        y = (int) vector.y;
        z = (int) vector.z;
        return this;
    }

    public Position add(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Position add(Vector3 vector) {
        x += (int) vector.x;
        y += (int) vector.y;
        z += (int) vector.z;
        return this;
    }

    public Position add(Position position) {
        x += position.x;
        y += position.y;
        z += position.z;
        return this;
    }

    public Position cpy() {
        return new Position(this);
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Position))
            return false;
        Position position = (Position) o;
        return position.x == x && position.y == y && position.z == z;
    }
}
