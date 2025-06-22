package secondEngine.components.helpers;

import secondEngine.objects.GameObject;

public abstract class Interactable {
    public GameObject gameObject;
    private boolean isActive;

    public abstract void interact();
    
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    
}
