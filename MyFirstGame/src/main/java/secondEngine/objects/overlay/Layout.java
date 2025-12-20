package secondEngine.objects.overlay;

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

        public enum Other implements SlotType {
            NULL
        }

        public default <SubType extends SlotType> boolean isOfType(Class<SubType> subType) {
            return this.getClass().isAssignableFrom(subType);
        }
    }

    private SlotType[][] layout;

    /**
     * Used when something can only store one thing
     * 
     * @param slotType
     */
    public Layout(SlotType slotType) {
        this.layout = new SlotType[1][1];
        this.layout[0][0] = slotType;
    }

    /**
     * 
     * @param layout A 2d layout mapping slot type to location
     */
    public Layout(SlotType[][] layout) {
        this.layout = layout;
    }

    public SlotType[][] getLayout() {
        return layout;
    }

    public boolean isInteractable(int i, int j) {
        return layout[i][j].getClass().isAssignableFrom(SlotType.Interactable.class);
    }
}
