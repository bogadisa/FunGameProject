package secondEngine.components.helpers;

import java.util.Optional;

import org.joml.Vector3f;

import secondEngine.components.Inventory;
import secondEngine.grid.GriddableSlot;
import secondEngine.objects.overlay.Layout.SlotType;
import secondEngine.util.PrefabFactory;
import secondEngine.util.PrefabFactory.PrefabIds;

public class InventorySlot extends GriddableSlot {
    private Inventory inventory;
    private PrefabIds objectId;
    private int amount;
    private int maxAmount;
    private boolean stackable = true;

    private boolean dirtySprite = false;
    private boolean dirtyAmount = false;

    public InventorySlot(Inventory inventory, int maxAmount) {
        this(inventory, null, 0, maxAmount);
    }

    public InventorySlot(Inventory inventory, PrefabIds objectId, int amount, int maxAmount) {
        assert maxAmount > 0 : "Inventory slots cannot store a negative amount";
        assert amount < maxAmount : "Inventory slots cannot store more than the max amount";
        this.slotType = SlotType.Interactable.INVENTORY;
        this.inventory = inventory;
        this.objectId = objectId;
        this.amount = amount;
        this.maxAmount = maxAmount;

        if (maxAmount == 1) {
            this.stackable = false;
        }
    }

    public boolean isEmpty() {
        return amount == 0 && objectId == null;
    }

    private boolean setToEmpty(boolean override) {
        if (!override && amount != 0) {
            return false;
        }
        this.objectId = null;
        this.dirtySprite = true;
        this.inventory.setInventorySlotEmpty(this);
        return true;
    }

    private boolean setToOccupied(PrefabIds objectId) {
        if (this.objectId != null || objectId == null) {
            return false;
        }
        this.objectId = objectId;
        this.dirtySprite = true;
        this.inventory.setInventorySlotOccupied(this);
        return true;
    }

    private boolean transfer(InventorySlot donator, InventorySlot recipient, int amount) {
        // TODO should this happen? or should the donator and recipient then become
        // donator? -- probably would lead to unintuitive behavior
        if (amount <= 0 || donator.isEmpty()) {
            return false;
        } else if (recipient.isEmpty()) {
            recipient.setToOccupied(donator.objectId);
        } else if (!recipient.equals(donator)) {
            return false;
        }
        this.dirtyAmount = true;
        boolean transferedAll = true;
        if (amount >= donator.amount) {
            amount = donator.amount;
            transferedAll = amount == donator.amount;
            donator.setToEmpty(true);
        }
        int combinedAmount = amount + recipient.amount;
        if (combinedAmount <= recipient.maxAmount) {
            recipient.amount = combinedAmount;
            donator.amount -= amount;
            return transferedAll;
        }
        transferedAll = false;
        recipient.amount = recipient.maxAmount;
        donator.amount = combinedAmount - recipient.maxAmount;
        return transferedAll;
    }

    public boolean transferFrom(InventorySlot donor) {
        return transfer(donor, this, donor.amount);
    }

    public boolean transferFrom(InventorySlot donor, int amount) {
        return transfer(donor, this, amount);
    }

    public boolean transferTo(InventorySlot recipient) {
        return transfer(this, recipient, this.amount);
    }

    public boolean transferTo(InventorySlot recipient, int amount) {
        return transfer(this, recipient, amount);
    }

    public int getAmount() {
        return this.amount;
    }

    public boolean isStackable() {
        return stackable;
    }

    public Optional<Sprite> getSprite() {
        return PrefabFactory.getObjectSprite(objectId);
    }

    public boolean isDirtySprite() {
        return dirtySprite;
    }

    public void setCleanSprite() {
        this.dirtySprite = false;
    }

    public boolean isDirtyAmount() {
        return dirtyAmount;
    }

    public void setDirtyAmount(boolean dirtyAmount) {
        this.dirtyAmount = dirtyAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != InventorySlot.class) {
            return false;
        }
        InventorySlot other = (InventorySlot) o;
        return this.objectId == null || this.objectId.equals(other.objectId);
    }

    @Override
    public int getObjectId() {
        return this.hashCode();
    }

    @Override
    public Vector3f getPosition() {
        // return new
        // Vector3f(this.inventory.gameObject.getPosition()).add(getOffset());
        return new Vector3f(getOffset());
    }
}
