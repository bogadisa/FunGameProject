package secondEngine.components;

import java.util.HashMap;
import java.util.Map;

import secondEngine.Component;
import secondEngine.components.helpers.InteractableState;
import secondEngine.objects.GameObject;

public class InteractiveStateMachine extends Component {
    private Map<String, InteractableState> interactables = new HashMap<>();

    public void addState(InteractableState state) {
        assert !this.interactables.containsKey(state.getTitle()) : "Duplicate interactables";
        this.interactables.put(state.getTitle(), state);
        state.start(this.gameObject);
    }

    public boolean interact(String interactType, GameObject otherGo) {
        InteractableState triggeredState = interactables.get(interactType);
        if (triggeredState.isActive()) {
            return triggeredState.interact(otherGo);
        } else {
            return false;
        }
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
