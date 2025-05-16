package secondEngine.components;

import secondEngine.Component;
import secondEngine.components.helpers.InventorySlot;

public class Inventory extends Component{
    private InventorySlot[] slots;
    private boolean[] slotIsTaken;
    private int nSlots, defaultSlotMaxAmount;


    
    public Inventory init(int nSlots, int defaultSlotMaxAmount) {
        this.nSlots = nSlots;
        this.slots = new InventorySlot[nSlots];
        this.slotIsTaken = new boolean[nSlots];
        this.defaultSlotMaxAmount = defaultSlotMaxAmount;
        return this;
    }

    public boolean add(InventorySlot newObject, int index) {
        return slots[index].add(newObject);
    }

    public boolean add(InventorySlot newObject) {
        boolean added = false;
        int minIndexOpenSlot = -1;
        for (int i = 0; i < nSlots; i++) {
            if (slotIsTaken[i]) {
                added = slots[i].add(newObject);
            } else {
                if (minIndexOpenSlot == -1) {
                    minIndexOpenSlot = i;
                }
            }
        }
        if (minIndexOpenSlot != -1) {
            slots[minIndexOpenSlot] = new InventorySlot(newObject, this.defaultSlotMaxAmount);
            slotIsTaken[minIndexOpenSlot] = true;
            if (newObject.amount == 0) {
                added = true;
            }
        }
        return added;
    }

    public boolean transfer(InventorySlot recipient, int index) {
        boolean transfered = slots[index].transfer(recipient);
        if (transfered) {
            // removing the object from the inventory
            slotIsTaken[index] = false;
            slots[index] = null;
        }
        return transfered;
    }


    @Override
    public void start() {
    }

    @Override
    public void update(float dt) {
    }

}
