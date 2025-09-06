package secondEngine.components.helpers;

import secondEngine.util.InteractableFactory;
import secondEngine.util.InteractableFactory.InteractableFunction;
import secondEngine.util.InteractableFactory.InteractableIds;

public class InteractableFrame {
        public InteractableIds interactId;
        public transient InteractableFunction interactable;

        public InteractableFrame(InteractableIds interactId) {
            this.interactId = interactId;
            this.interactable = InteractableFactory.getInteractable(interactId);
        }
}
