package secondEngine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import secondEngine.Config.CameraConfig;

public class Camera {
    public Vector2f position;
    private Matrix4f projectionMatrix, viewMatrix;

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection() {
        projectionMatrix.identity();
        // Defines it as 40x21 units, where 1 unit is a 32x32 pixel square
        // TODO define units elsewhere

        // old
        // projectionMatrix.ortho(0.0f, 32.0f * 40.0f, 0.0f, 32.0f * 21.0f, 0.0f, 100.0f);
        // new
        projectionMatrix.ortho(0.0f, CameraConfig.width, 0.0f, CameraConfig.height, 0.0f, 100.0f);
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();
        this.viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f), cameraFront.add(position.x, position.y, 0.0f), cameraUp);

        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Vector2f worldToScreen(Vector2f world) {
        return new Vector2f(world.x - position.x, world.y - position.y);
    }

    public Vector2f worldToScreen(Vector3f world) {
        return worldToScreen(world.xy(new Vector2f()));
    }
}
