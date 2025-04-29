package secondEngine.components;

import org.joml.Vector3f;

import secondEngine.Component;

public class Transform extends Component {

    public Vector3f position = new Vector3f();
    public Vector3f scale = new Vector3f();

    // public Transform() {
    //     init(new Vector3f(), new Vector3f());
    // }

    // public Transform(Vector3f position) {
    //     init(position, new Vector3f());
    // }

    // public Transform(Vector3f position, Vector3f scale) {
    //     init(position, scale);
    // }

    public Transform init(Vector3f position, Vector3f scale) {
        this.position = position;
        this.scale = scale;

        return this;
    }

    public Transform copy() {
        return new Transform().init(new Vector3f(this.position), new Vector3f(this.scale));
    }

    public void copy(Transform to) {
        to.position.set(this.position);
        to.scale.set(this.scale);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Transform)) return false;

        Transform t = (Transform)o;
        return t.position.equals(this.position) && t.scale.equals(this.scale);
    }

    
    @Override
    public void start() {
    }

    @Override
    public void update(float dt) {
    }
}