package secondEngine.components.helpers;

public class InventorySlot {
    public int objectId, amount;
    private int maxAmount;
    private boolean stackable = true;

    public InventorySlot(InventorySlot donator, int maxAmount) {
        this(donator.objectId, donator.amount < maxAmount ? donator.amount : maxAmount, maxAmount);
        donator.amount = donator.amount < maxAmount ? 0 : donator.amount - maxAmount;
    }

    public InventorySlot(int objectId, int amount, int maxAmount) {
        assert maxAmount > 0 : "Inventory slots cannot store a negative amount";
        assert amount < maxAmount: "Inventory slots cannot store more than the max amount";
        this.objectId = objectId;
        this.amount = amount;
        this.maxAmount = maxAmount;

        if (maxAmount == 1) {
            this.stackable = false;
        }
    }


    

    private boolean transfer(InventorySlot donator, InventorySlot recipient) {
        if (recipient.equals(donator)) {
            int combinedAmount = donator.amount + recipient.amount;
            if (combinedAmount <= recipient.maxAmount) {
                recipient.amount = combinedAmount;
                donator.amount = 0;
                return true;
            }
            recipient.amount = recipient.maxAmount;
            donator.amount = combinedAmount - recipient.maxAmount;
        }
        return false;
    }

    public boolean add(InventorySlot donator) {
        return transfer(donator, this);
    }

    public boolean transfer(InventorySlot recipient) {
        return transfer(this, recipient);
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != InventorySlot.class) return false;
        InventorySlot other = (InventorySlot)o;
        return this.objectId == other.objectId; 
    }
}
