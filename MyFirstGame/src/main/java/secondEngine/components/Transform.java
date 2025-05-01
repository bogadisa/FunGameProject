package secondEngine.components;

import org.joml.Vector3f;

import secondEngine.Component;

public class Transform extends Component {

    public Vector3f position = new Vector3f();
    public Vector3f scale = new Vector3f();
    public float rotation = 0.0f;

    public Transform init(Vector3f position, Vector3f scale) {
        return init(position, scale, 0.0f);

    }

    public Transform init(Vector3f position, Vector3f scale, float rotation) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;

        return this;
    }

    public Transform copy() {
        return new Transform().init(new Vector3f(this.position), new Vector3f(this.scale), this.rotation);
    }

    public void copy(Transform to) {
        to.position.set(this.position);
        to.scale.set(this.scale);
        to.rotation = this.rotation;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Transform)) return false;

        Transform t = (Transform)o;
        return t.position.equals(this.position) && t.scale.equals(this.scale) && t.rotation == this.rotation;
    }

    
    @Override
    public void start() {
    }

    @Override
    public void update(float dt) {
    }
}