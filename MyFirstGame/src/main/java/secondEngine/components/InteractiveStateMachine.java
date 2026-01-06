package secondEngine.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import secondEngine.Component;
import secondEngine.Window;
import secondEngine.components.helpers.InteractableState;
import secondEngine.grid.SpatialGrid;
import secondEngine.objects.GameObject;

public class InteractiveStateMachine extends Component {
    private Map<String, InteractableState> interactables = new HashMap<>();
    private Map<String, List<GameObject>> linkedGameObjects = new HashMap<>();

    public void addState(InteractableState state) {
        assert !this.interactables.containsKey(state.getTitle()) : "Duplicate interactables";
        this.interactables.put(state.getTitle(), state);
        state.start(this.gameObject);
    }

    public void link(String interactType, GameObject otherGo) {
        List<GameObject> gos = linkedGameObjects.getOrDefault(interactType, new ArrayList<>());
        gos.add(otherGo);
    }

    public boolean interact(String interactType) {
        List<GameObject> gos;
        if (linkedGameObjects.containsKey(interactType)) {
            gos = linkedGameObjects.get(interactType);
        } else {
            SpatialGrid grid = Window.getScene().worldGrid();
            gos = grid.getObjects(this.gameObject, GameObject.class);
        }
        boolean success = false;
        for (GameObject go : gos) {
            success = interact(interactType, go) || success;
        }
        return success;
    }

    public boolean interact(String interactType, GameObject otherGo) {
        InteractableState triggeredState = interactables.get(interactType);
        if (triggeredState.isActive()) {
            return triggeredState.interact(otherGo);
        }
        return false;
    }

    @Override
    public void start() {
        for (InteractableState interactableState : interactables.values()) {
            interactableState.start(this.gameObject);
        }
    }

    @Override
    public void update(float dt) {
    }
}
