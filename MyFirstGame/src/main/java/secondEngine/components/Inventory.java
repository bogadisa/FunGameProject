package secondEngine.components;

import java.util.HashSet;
import java.util.Set;

import secondEngine.Component;
import secondEngine.components.helpers.InventorySlot;

public class Inventory extends Component {
    // private Map<String, InventorySlot> slots;
    private InventorySlot[] slots;
    private SpriteRenderer[] sprites;
    private Set<Integer> empty;
    private Set<Integer> occupied;
    // private String[] takenSlots;
    // private int defaultSlotMaxAmount;

    public Inventory init(int nInventorySlots, int defaultSlotMaxAmount) {
        return init(nInventorySlots, defaultSlotMaxAmount, false);
    }

    public Inventory init(int nInventorySlots, int defaultSlotMaxAmount, boolean hasOverlay) {
        slots = new InventorySlot[nInventorySlots];
        sprites = new SpriteRenderer[nInventorySlots];
        empty = new HashSet<>();
        occupied = new HashSet<>();
        for (int i = 0; i < nInventorySlots; i++) {
            slots[i] = new InventorySlot(this, defaultSlotMaxAmount);
            empty.add(i);
        }
        if (hasOverlay) {
            Overlay overlay = this.gameObject.getComponent(Overlay.class);
            for (int i = 0; i < slots.length; i++) {
                boolean wasAdded = overlay.addSlot(slots[i]);
                assert wasAdded : "Ran out of room when adding inventory slots: Needed " + slots.length
                        + ", only found " + (i + 1);
            }

        }
        return this;
    }

    public void setInventorySlotEmpty(InventorySlot slotToEmpty) {
        setInventorySlotStatus(slotToEmpty, true);
    }

    public void setInventorySlotOccupied(InventorySlot slotToEmpty) {
        setInventorySlotStatus(slotToEmpty, false);
    }

    private void setInventorySlotStatus(InventorySlot slotToChange, boolean isEmpty) {
        InventorySlot slot;
        for (int i = 0; i < slots.length; i++) {
            slot = slots[i];
            if (slot == slotToChange) {
                if (isEmpty && slotToChange.isEmpty()) {
                    empty.add(i);
                    occupied.remove((Integer) i);
                }
                if (!isEmpty && !slotToChange.isEmpty()) {
                    empty.remove((Integer) i);
                    occupied.add(i);
                }
            }
        }
    }

    public InventorySlot getInventorySlot(int index) {
        return slots[index];
    }

    public boolean isInventorySlotEmpty(int index) {
        return empty.contains(index);
    }

    public InventorySlot[] getInventorySlots() {
        return slots;
    }

    public boolean transferFrom(InventorySlot donator, int amount) {
        boolean transferedAll = false;
        int remainingAmount = donator.getAmount() - amount;
        // TODO should look for a matching inventory slot before finding an empty slot
        for (Integer index : occupied) {
            if (slots[index].equals(donator)) {
                transferedAll = slots[index].transferFrom(donator, amount);
                if (transferedAll) {
                    return true;
                }
                amount = donator.getAmount() - remainingAmount;
            }
        }
        for (int i = 0; i < slots.length; i++) {
            if (slots[i].equals(donator)) {
                transferedAll = slots[i].transferFrom(donator, amount);
                if (transferedAll) {
                    return true;
                }
                amount = donator.getAmount() - remainingAmount;
            }
        }
        return transferedAll;
    }

    public InventorySlot addSpriteRenderer(SpriteRenderer sprite) {
        for (int i = 0; i < slots.length; i++) {
            if (sprites[i] == null) {
                sprites[i] = sprite;
                return slots[i];
            }
        }
        assert true : "More sprite renderers were added than there were inventory slots";
        return null;
    }

    @Override
    public void start() {
        SpriteRenderer sprite;
        CompositeSpriteRenderer compSprite = this.gameObject.getComponent(CompositeSpriteRenderer.class);
        if (compSprite != null) {
            for (int i = 0; i < slots.length; i++) {
                InventorySlot slot = slots[i];
                if (sprites[i] == null) {
                    sprite = new SpriteRenderer();
                    sprites[i] = sprite;
                    compSprite.addSpriteRenderer(sprite, slot.getScale(), slot.getOffset());
                }
            }
        }
        for (int i = 0; i < slots.length; i++) {
            InventorySlot slot = slots[i];

            sprite = sprites[i];
            if (sprite != null && slot.getSprite().isPresent()) {
                sprite.setSprite(slot.getSprite().get());
                slot.setCleanSprite();
            }
        }

    }

    @Override
    public void update(float dt) {
        for (int i = 0; i < slots.length; i++) {
            InventorySlot slot = slots[i];
            if (slot.isDirtySprite()) {
                SpriteRenderer sprite = sprites[i];
                if (sprite != null && slot.getSprite().isPresent()) {
                    sprite.setSprite(slot.getSprite().get());
                    slot.setCleanSprite();
                }
            }
        }
    }

}
