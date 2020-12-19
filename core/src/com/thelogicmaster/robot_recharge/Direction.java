package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public enum Direction {

    NORTH(new Quaternion(), new Vector3(1, 0, 0)),
    WEST(new Quaternion(Vector3.Y, 90), new Vector3(0, 0, -1)),
    SOUTH(new Quaternion(Vector3.Y, 180), new Vector3(-1, 0, 0)),
    EAST(new Quaternion(Vector3.Y, 270), new Vector3(0, 0, 1));

    private final Quaternion quaternion;
    private final Vector3 vector;

    Direction(Quaternion quaternion, Vector3 vector) {
        this.quaternion = quaternion;
        this.vector = vector;
    }

    public Quaternion getQuaternion() {
        return quaternion;
    }

    public Vector3 getVector() {
        return vector;
    }

    public static Direction fromYaw(float yaw) {
        int normalized = RobotUtils.modulus((int) Math.floor(yaw), 360);
        if (normalized <= 45 || normalized > 315)
            return NORTH;
        else if (normalized <= 135)
            return WEST;
        else if (normalized <= 225)
            return SOUTH;
        else
            return EAST;
    }
}
