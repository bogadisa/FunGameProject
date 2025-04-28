package secondEngine;

import secondEngine.objects.GameObject;

public abstract class Component {

    public transient GameObject gameObject = null;

    public abstract void start();

    public abstract void update(float dt);
}