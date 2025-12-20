package secondEngine.data;

import java.util.HashMap;

import secondEngine.Component;
import secondEngine.components.Inventory;
import secondEngine.components.MouseTracker;
import secondEngine.components.PlayerControls;

public class UpdateHierarchy {
    public enum Priority {
        FIRST, SECOND, THIRD, LAST
    }

    private static UpdateHierarchy updateHierarchy;
    private static HashMap<Class<? extends Component>, Priority> hierarchy;

    private UpdateHierarchy() {
        hierarchy = new HashMap<>();
        hierarchy.put(PlayerControls.class, Priority.FIRST);
        hierarchy.put(MouseTracker.class, Priority.FIRST);
        hierarchy.put(Inventory.class, Priority.SECOND);
    }

    static public UpdateHierarchy get() {
        if (UpdateHierarchy.updateHierarchy == null) {
            UpdateHierarchy.updateHierarchy = new UpdateHierarchy();
        }
        return UpdateHierarchy.updateHierarchy;
    }

    public Priority getPriority(Component c) {
        return hierarchy.getOrDefault(c.getClass(), Priority.LAST);
    }
}
