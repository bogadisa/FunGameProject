package secondEngine.objects;

import java.util.ArrayList;
import java.util.List;

import secondEngine.Component;
import secondEngine.components.Transform;

public class GameObject {

    private String name;
    private transient int objectId;

    private transient boolean serializeOnSave = true;

    private List<Component> components;
    public transient Transform transform;

    public GameObject(String name, int objectId) {
        this.name = name;
        this.objectId = objectId;
        this.components = new ArrayList<>();
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error: Casting component.";
                }
            }
        }

        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component c) {
        this.components.add(c);
        c.gameObject = this;

        if (c.getClass().equals(Transform.class)) {
            this.transform = (Transform) c;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != GameObject.class) return false;
        GameObject go = (GameObject) o;
        return go.getObjectId() == this.objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getObjectId() {
        return objectId;
    }

    public boolean serializeOnSave() {
        return serializeOnSave;
    }

    public void setSerializeOnSave(boolean serializeOnSave) {
        this.serializeOnSave = serializeOnSave;
    }

    public void update(float dt) {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).update(dt);
        }
    }

    public void start() {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).start();
        }
    }
}