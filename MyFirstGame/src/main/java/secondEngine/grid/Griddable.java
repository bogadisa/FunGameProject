package secondEngine.grid;

import org.joml.Vector3f;

public interface Griddable {
    public Vector3f getPosition();

    public Vector3f getScale();

    public int getObjectId();

    public default <GridObject extends Griddable> boolean isOfType(Class<GridObject> objectClass) {
        return objectClass.isAssignableFrom(this.getClass());
    }
}
