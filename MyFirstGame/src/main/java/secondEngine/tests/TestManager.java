package secondEngine.tests;

import secondEngine.Component;
import secondEngine.components.Inventory;

public class TestManager {

    public static <ComponentType extends Component> void testComponent(Class<ComponentType> componentToTest) {
        if (componentToTest == Inventory.class) {
            InventoryTest.runTest();
        }
    }
}
