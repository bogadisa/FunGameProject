package secondEngine.components.helpers;

import secondEngine.objects.GameObject;
import secondEngine.util.InteractableFactory;
import secondEngine.util.InteractableFactory.InteractableFunction;
import secondEngine.util.InteractableFactory.InteractableIds;

public class InteractableFrame {
    private InteractableIds interactId;
    private transient InteractableFunction interactable;

    public InteractableFrame(InteractableIds interactId) {
        this.interactId = interactId;
        this.interactable = InteractableFactory.getInteractable(interactId);
    }

    public void refresh() {
        this.interactable = InteractableFactory.getInteractable(interactId);
    }

    public boolean interact(GameObject thisGO, GameObject otherGO) {
        return interactable.interact(thisGO, otherGO);
    }
}
