package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class CameraController extends CameraInputController {

    public float minAngle = 10;
    public float maxAngle = 90;
    public float minDistance = 15;
    public float maxDistance = 45;

    private final Quaternion tempRot = new Quaternion();
    private final Vector3 tempVector1 = new Vector3();
    private final Vector3 tempVector2 = new Vector3();

    private boolean disabled;

    public CameraController(Camera camera) {
        super(camera);
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public void update() {
        if (disabled)
            return;
        super.update();
    }

    @Override
    public boolean zoom(float amount) {
        if (disabled)
            return true;
        float constrained;
        float distance = camera.position.dst(target);
        if (amount < 0)
            constrained = Math.max(amount, Math.min(0, distance - maxDistance));
        else
            constrained = Math.min(amount, Math.max(0, distance - minDistance));
        return super.zoom(constrained);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (disabled)
            return true;
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    protected boolean process(float deltaX, float deltaY, int button) {
        if (disabled)
            return true;
        if (button == rotateButton) {
            tempVector1.set(camera.direction).crs(camera.up).y = 0f;
            tempVector1.nor();
            float constrained;
            float angle = camera.view.getRotation(tempRot).getAngleAround(tempVector1);
            if (deltaY > 0)
                constrained = Math.min(deltaY, Math.max(0, angle - minAngle) / rotateAngle);
            else
                constrained = Math.max(deltaY, Math.min(0, angle - maxAngle) / rotateAngle);
            camera.rotateAround(target, tempVector1, constrained * rotateAngle);
            camera.rotateAround(target, Vector3.Y, deltaX * -rotateAngle);
        } else if (button == translateButton) {
            camera.translate(tempVector1.set(camera.direction).crs(camera.up).nor().scl(-deltaX * translateUnits));
            camera.translate(tempVector2.set(camera.up).scl(-deltaY * translateUnits));
            if (translateTarget)
                target.add(tempVector1).add(tempVector2);
        } else if (button == forwardButton) {
            camera.translate(tempVector1.set(camera.direction).scl(deltaY * translateUnits));
            if (forwardTarget)
                target.add(tempVector1);
        }
        if (autoUpdate)
            camera.update();
        return true;
    }
}
