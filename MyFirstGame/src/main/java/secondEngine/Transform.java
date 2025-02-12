package secondEngine;

import org.joml.Vector3f;

public class Transform {

    public Vector3f position;
    public Vector3f scale;

    public Transform() {
        init(new Vector3f(), new Vector3f());
    }

    public Transform(Vector3f position) {
        init(position, new Vector3f());
    }

    public Transform(Vector3f position, Vector3f scale) {
        init(position, scale);
    }

    public void init(Vector3f position, Vector3f scale) {
        this.position = position;
        this.scale = scale;
    }
}