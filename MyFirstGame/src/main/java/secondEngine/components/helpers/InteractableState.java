package secondEngine.components.helpers;

import java.util.ArrayList;
import java.util.List;

import secondEngine.objects.GameObject;
import secondEngine.util.InteractableFactory;
import secondEngine.util.Time;
import secondEngine.util.Callback;
import secondEngine.util.InteractableFactory.InteractableIds;

public class InteractableState {
    private String title;

    public List<InteractableFrame> interactableFrames = new ArrayList<>();
    private boolean isActive = true;
    private int cooldown = 5;
    private transient Callback<Boolean> cooldownCallback = null;
    private transient GameObject gameObject;

    public InteractableState init(String title) {
        return init(title, 5);
    }

    public InteractableState init(String title, int cooldown) {
        this.title = title;
        this.cooldown = cooldown;
        return this;
    }

    public void start(GameObject gameObject) {
        this.gameObject = gameObject;
        refreshInteractables();
    }

    private void refreshInteractables() {
        for (InteractableFrame interactableFrame : interactableFrames) {
            interactableFrame.refresh();
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getTitle() {
        return title;
    }

    public void addFrame(InteractableIds interactableId) {
        interactableFrames.add(new InteractableFrame(interactableId));
    }

    public void addFrames(List<InteractableIds> interactableIds) {
        for (InteractableIds interactableId : interactableIds) {
            addFrame(interactableId);
        }
    }

    public boolean interact(GameObject otherGo) {
        if (cooldownCallback != null) {
            boolean ready = cooldownCallback.call();
            if (!ready) {
                return false;
            }
        }
        boolean interacted = false;
        for (InteractableFrame interactableFrame : interactableFrames) {
            interacted = interactableFrame.interact(this.gameObject, otherGo);
            if (!interacted) {
                return false;
            }
        }
        this.cooldownCallback = Time.scheduleCooldown(title, this.cooldown);
        return interacted;
    }
}
