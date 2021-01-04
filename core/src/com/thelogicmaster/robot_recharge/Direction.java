package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public enum Direction {

    NORTH(0, new Vector3(1, 0, 0)),
    WEST(90, new Vector3(0, 0, -1)),
    SOUTH(180, new Vector3(-1, 0, 0)),
    EAST(270, new Vector3(0, 0, 1));

    private final Quaternion quaternion;
    private final Vector3 vector;
    private final int angle;

    Direction(int angle, Vector3 vector) {
        this.quaternion = new Quaternion(Vector3.Y, angle);
        this.vector = vector;
        this.angle = angle;
    }

    public Quaternion getQuaternion() {
        return quaternion;
    }

    public Vector3 getVector() {
        return vector;
    }

    public int getAngle() {
        return angle;
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
