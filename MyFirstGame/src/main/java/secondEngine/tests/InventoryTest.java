package secondEngine.tests;

import secondEngine.components.Inventory;
import secondEngine.components.helpers.InventorySlot;
import secondEngine.objects.Tiles.GroundManager.GroundPrefabs;

public class InventoryTest extends TestBase {
    public static void runTest() {
        System.out.println("Running inventory component test");
        InventoryTest.testIndependentTransfer();
        InventoryTest.testShiftClickItemTransfer();
    }

    private static void testIndependentTransfer() {
        String name = "Independent transfer";
        Inventory inventoryObj = new Inventory().init(2, 64);
        InventorySlot slot1 = new InventorySlot(inventoryObj, GroundPrefabs.Spring.DIRT_1, 1, 64);
        InventorySlot slot2 = new InventorySlot(inventoryObj, GroundPrefabs.Spring.DIRT_1, 5, 64);
        boolean transferedAll = slot1.transferTo(slot2, 1);
        InventoryTest.test(transferedAll, name, "Failed to transfer all");
        InventoryTest.test(slot1.isEmpty(), name, "Slot was not set to empty");
        InventoryTest.test(slot2.getAmount() == 6, name, "Slot was not set to the right amount");
        printTestResults(3, name);
    }

    private static void testShiftClickItemTransfer() {
        String name = "Shift Click transfer";
        Inventory inventoryObj = new Inventory().init(2, 5);
        Inventory inventoryObj2 = new Inventory().init(2, 5);
        InventorySlot slot1 = new InventorySlot(inventoryObj2, GroundPrefabs.Spring.DIRT_1, 1, 64);
        InventorySlot slot2 = new InventorySlot(inventoryObj2, GroundPrefabs.Spring.DIRT_1, 5, 64);
        slot1.transferTo(inventoryObj.getInventorySlot(1), 1);
        InventoryTest.test(inventoryObj.getInventorySlot(1).getAmount() == 1, name,
                "Did not transfer to the specified slot");
        boolean transferedAll = inventoryObj.transferFrom(slot2, 5);
        InventoryTest.test(transferedAll, name, "Failed to transfer all");
        InventoryTest.test(inventoryObj.getInventorySlot(1).getAmount() == 5, name,
                "Did not transfer to the a slot that was already occupied");
        printTestResults(3, name);
    }
}
