package secondEngine.components;

import java.util.HashMap;
import java.util.Map;

import secondEngine.Component;
import secondEngine.components.helpers.InteractableState;
import secondEngine.objects.GameObject;

public class InteractiveStateMachine extends Component {
    private Map<String, InteractableState> interactables = new HashMap<>();


    public void addState(InteractableState state) {
        this.interactables.put(state.title, state);
        state.refreshInteractables();
    }

    public void interact(String interactType, GameObject go) {
        InteractableState triggeredState = interactables.get(interactType);
        if (triggeredState.isActive()) triggeredState.interact(go);
    }

    @Override
    public void start() {
        for (InteractableState interactableState : interactables.values()) {
            interactableState.refreshInteractables();
        }
    }

    @Override
    public void update(float dt) {
    }
}
