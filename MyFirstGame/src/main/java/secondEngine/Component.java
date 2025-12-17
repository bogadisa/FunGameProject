package secondEngine;

import secondEngine.data.UpdateHierarchy;
import secondEngine.data.UpdateHierarchy.Priority;
import secondEngine.objects.GameObject;

public abstract class Component {

    public transient GameObject gameObject = null;
    private Priority priority = UpdateHierarchy.get().getPriority(this);

    public Priority getPriority() {
        return priority;
    }

    public abstract void start();

    public abstract void update(float dt);
}