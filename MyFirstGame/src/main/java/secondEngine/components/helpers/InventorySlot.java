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
    private PrefabIds prefabId;
    private int amount;

    private int maxAmount;
    private boolean stackable = true;

    private boolean dirtySprite = false;
    private boolean dirtyAmount = false;

    public InventorySlot(Inventory inventory, int maxAmount) {
        this(inventory, null, 0, maxAmount);
    }

    public InventorySlot(Inventory inventory, PrefabIds prefabId, int amount, int maxAmount) {
        assert maxAmount > 0 : "Inventory slots cannot store a negative amount";
        assert amount < maxAmount : "Inventory slots cannot store more than the max amount";
        this.slotType = SlotType.Interactable.INVENTORY;
        this.inventory = inventory;
        this.prefabId = prefabId;
        this.amount = amount;
        this.maxAmount = maxAmount;

        if (maxAmount == 1) {
            this.stackable = false;
        }
    }

    public boolean isEmpty() {
        return amount == 0 && prefabId == null;
    }

    private boolean setToEmpty(boolean override) {
        if (!override && amount != 0) {
            return false;
        }
        this.setPrefabId(null);
        this.inventory.setInventorySlotEmpty(this);
        return true;
    }

    private boolean setToOccupied(PrefabIds prefabId) {
        if (this.prefabId != null || prefabId == null) {
            return false;
        }
        this.setPrefabId(prefabId);
        this.inventory.setInventorySlotOccupied(this);
        return true;
    }

    public boolean exchangeWith(InventorySlot recipient) {
        return exchange(this, recipient);
    }

    private boolean exchange(InventorySlot donator, InventorySlot recipient) {
        if (donator.prefabId != null && donator.maxAmount < recipient.amount
                || recipient.prefabId != null && recipient.maxAmount < donator.amount) {
            return false;
        }
        if (donator.prefabId == recipient.prefabId) {
            return transfer(donator, recipient, donator.amount);
        }
        PrefabIds id1 = donator.getPrefabId();
        donator.setPrefabId(recipient.getPrefabId());
        recipient.setPrefabId(id1);

        int amount1 = donator.getAmount();
        donator.setAmount(recipient.getAmount());
        recipient.setAmount(amount1);
        return true;
    }

    private boolean transfer(InventorySlot donator, InventorySlot recipient, int amount) {
        // TODO should this happen? or should the donator and recipient then become
        // donator? -- probably would lead to unintuitive behavior
        if (amount <= 0 || donator.isEmpty()) {
            return false;
        } else if (recipient.isEmpty()) {
            recipient.setToOccupied(donator.prefabId);
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
            recipient.setAmount(combinedAmount);
            donator.setAmount(donator.getAmount() - amount);
            return transferedAll;
        }
        transferedAll = false;
        recipient.setAmount(recipient.maxAmount);
        donator.setAmount(combinedAmount - recipient.maxAmount);
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

    public void setAmount(int amount) {
        this.amount = amount;
        this.dirtyAmount = true;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setPrefabId(PrefabIds prefabId) {
        this.prefabId = prefabId;
        this.dirtySprite = true;
    }

    public PrefabIds getPrefabId() {
        return this.prefabId;
    }

    public boolean isStackable() {
        return stackable;
    }

    public Optional<Sprite> getSprite() {
        return PrefabFactory.getObjectSprite(prefabId);
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
        return this.prefabId == null || this.prefabId.equals(other.prefabId);
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
