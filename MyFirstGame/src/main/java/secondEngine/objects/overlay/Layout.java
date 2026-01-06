package secondEngine.objects.overlay;

import org.joml.Vector2i;

/**
 * Used for anything that is a UI or interacts with a UI
 */
public class Layout {
    /**
     * Used to determine characteristics
     */
    public interface SlotType {
        public enum Interactable implements SlotType {
            INVENTORY
        }

        public enum Text implements SlotType {
            POS
        }

        public enum Other implements SlotType {
            NULL
        }

        public default <SubType extends SlotType> boolean isOfType(Class<SubType> subType) {
            return this.getClass().isAssignableFrom(subType);
        }
    }

    private Vector2i scale;
    private SlotType[][] layout;
    private boolean[][] occupied;

    /**
     * Used when something can only store one thing
     * 
     * @param slotType
     */
    public Layout(SlotType slotType) {
        this.scale = new Vector2i(1, 1);
        this.layout = new SlotType[scale.y][scale.x];
        this.layout[0][0] = slotType;
        this.occupied = new boolean[scale.y][scale.x];

    }

    /**
     * 
     * @param layout A 2d layout mapping slot type to location
     */
    public Layout(SlotType[][] layout) {
        this.layout = layout;
        this.scale = new Vector2i(layout[0].length, layout.length);
        for (int i = 0; i < scale.x; i++) {
            for (int j = 0; j < scale.y; j++) {
                if (layout[j][i] == null) {
                    layout[j][i] = SlotType.Other.NULL;
                }
            }
        }
        this.occupied = new boolean[scale.y][scale.x];
    }

    public Vector2i getScale() {
        return this.scale;
    }

    public SlotType[][] getLayout() {
        return layout;
    }

    public boolean isInteractable(int i, int j) {
        return layout[j][i].getClass().isAssignableFrom(SlotType.Interactable.class);
    }

    public void setOccupied(int i, int j, boolean occupied) {
        this.occupied[j][i] = occupied;
    }

    public boolean isOccupied(int i, int j) {
        return occupied[j][i];
    }

    public Layout copy() {
        return new Layout(layout);
    }
}
