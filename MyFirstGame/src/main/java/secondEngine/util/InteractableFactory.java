package secondEngine.util;

import java.util.HashSet;
import java.util.Set;

import secondEngine.Window;
import secondEngine.components.GridMachine;
import secondEngine.components.InteractiveStateMachine;
import secondEngine.components.helpers.GridState;
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
            ENLARGE,
            HIGHLIGHT,
            NO_HIGHLIGHT;

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
            case ENLARGE:
                return get().new InteractableFunction() {
                    @Override
                    public void interact(GameObject thisGO, GameObject otherGO) {
                        otherGO.transform.scale.set(128, 256, 1);
                    }
                };

            case HIGHLIGHT:
                return get().new HighlightInteractableFunction() {
                    @Override
                    public void interact(GameObject thisGO, GameObject otherGO) {
                        Set<String> otherCoverage = getCoverage(otherGO);
                        Set<String> thisCoverage = getCoverage(thisGO);
                        boolean overlap = otherCoverage.retainAll(thisCoverage);
                        if (overlap) {
                            
                        }
                        
                    }
                    
                };
            
            case NO_HIGHLIGHT:
                break;
                
            default:
                break;
        }

        return null;
    }

    public abstract class InteractableFunction {
        public abstract void interact(GameObject thisGO, GameObject otherGO);
    }

    private abstract class HighlightInteractableFunction extends InteractableFunction {
        protected Set<String> getCoverage(GameObject go) {
            GridMachine gm = go.getComponent(GridMachine.class);
            GridState gs = gm.getGridState(Window.getScene().worldGrid().getName());
            return new HashSet<>(gs.getCurrentGridCells());
        }
    }
}
