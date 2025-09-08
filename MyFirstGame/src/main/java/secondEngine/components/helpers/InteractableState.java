package secondEngine.components.helpers;

import java.util.ArrayList;
import java.util.List;

import secondEngine.objects.GameObject;
import secondEngine.util.InteractableFactory;
import secondEngine.util.InteractableFactory.InteractableIds;

public class InteractableState {
    public String title;
    public List<InteractableFrame> interactableFrames = new ArrayList<>();
    private boolean isActive = true;
    private transient GameObject gameObject;

    public void start(GameObject gameObject) {
        this.gameObject = gameObject;
        refreshInteractables();
    }

    private void refreshInteractables() {
        for (InteractableFrame interactableFrame : interactableFrames) {
            interactableFrame.interactable = InteractableFactory.getInteractable(interactableFrame.interactId);
        }
    }
    
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void addFrame(InteractableIds interactableId) {
        interactableFrames.add(new InteractableFrame(interactableId));
    }

    public void addFrames(List<InteractableIds> interactableIds) {
        for (InteractableIds interactableId : interactableIds) {
            this.addFrame(interactableId);
        }
    }
    
    public void interact(GameObject go) {
        for (InteractableFrame interactableFrame : interactableFrames) {
            interactableFrame.interactable.interact(this.gameObject, go);
        }
    }
}
