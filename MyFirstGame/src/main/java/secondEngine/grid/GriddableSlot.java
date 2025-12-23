package secondEngine.grid;

import org.joml.Vector3f;

import secondEngine.objects.overlay.Layout.SlotType;

public abstract class GriddableSlot implements Griddable {
    public SlotType slotType;
    private Vector3f offset = new Vector3f(0);

    public void setOffset(Vector3f offset) {
        this.offset = offset;
    }

    public Vector3f getOffset() {
        return this.offset;
    }
}
