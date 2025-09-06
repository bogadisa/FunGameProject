package secondEngine.util;

import secondEngine.Component;
import secondEngine.components.InteractiveStateMachine;
import secondEngine.objects.GameObject;
import secondEngine.util.InteractableFactory.InteractableIds.Misc;

/**
 * <pre>
 * Categories: 
 * <tab/> Misc.
 * <pre/>
 */
public class InteractableFactory {
    public interface InteractableIds {
        public abstract Categories id();

        enum Categories {
            MISC
        }

        enum Misc implements InteractableIds {
            TEST;

            @Override
            public Categories id() {
                return Categories.MISC;
            }
        }
    }

    private static InteractableFactory interactableFactory;

    private InteractableFactory() {}

    public static InteractableFactory get() {
        if (InteractableFactory.interactableFactory == null) {
            InteractableFactory.interactableFactory = new InteractableFactory();
        }
        return InteractableFactory.interactableFactory;
    }

    public static InteractableFunction getInteractable(InteractableIds interactable) {
        InteractableFunction func = null;
        switch (interactable.id()) {
            case MISC:
                func = InteractableFactory.getMisc(interactable);
            default:
                break;
        }
        if (func == null) {
            throw new IllegalArgumentException("Argument of type '" + interactable + "' is not supported");
        }
        return func;
    }

    private static InteractableFunction getMisc(InteractableIds subcategory) {
        switch (Misc.class.cast(subcategory)) {
            case TEST:
                return get().new InteractableFunction() {
                    @Override
                    public void interact(GameObject go) {
                        go.transform.scale.set(128, 256, 1);
                    }
                };
                
            default:
                break;
        }

        return null;
    }

    public abstract class InteractableFunction {
        protected InteractiveStateMachine state;
        public abstract void interact(GameObject go);
    }
}
