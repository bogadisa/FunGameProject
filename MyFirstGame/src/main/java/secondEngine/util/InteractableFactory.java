package secondEngine.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import secondEngine.Window;
import secondEngine.components.AnimationStateMachine;
import secondEngine.components.GridMachine;
import secondEngine.components.SpriteRenderer;
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
            NO_HIGHLIGHT,
            TOGGLE_HIGHLIGHT;

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
                    public boolean interact(GameObject thisGO, GameObject otherGO) {
                        otherGO.transform.scale.set(128, 256, 1);
                        return true;
                    }
                };

            case HIGHLIGHT:
                return get().new HighlightInteractableFunction() {
                    @Override
                    public boolean interact(GameObject thisGO, GameObject otherGO) {
                        return trigger("addColor", thisGO, otherGO);
                    }
                };
            
            case NO_HIGHLIGHT:
                return get().new HighlightInteractableFunction() {
                    @Override
                    public boolean interact(GameObject thisGO, GameObject otherGO) {
                        return trigger("removeColor", thisGO, otherGO);
                    }
                };

            case TOGGLE_HIGHLIGHT:
                return get().new HighlightInteractableFunction() {
                    @Override
                    public boolean interact(GameObject thisGO, GameObject otherGO) {
                        return trigger("toggleColor", thisGO, otherGO);
                    }
                };

                
            default:
                break;
        }

        return null;
    }

    public abstract class InteractableFunction {
        public abstract boolean interact(GameObject thisGO, GameObject otherGO);
    }

    private abstract class HighlightInteractableFunction extends InteractableFunction {
        private Set<String> getCoverage(GameObject go) {
            GridMachine gm = go.getComponent(GridMachine.class);
            GridState gs = gm.getGridState(Window.getScene().worldGrid().getName());
            return new HashSet<>(gs.getCurrentGridCells());
        }

        protected boolean trigger(String trigger, GameObject thisGO, GameObject otherGO) {
            AnimationStateMachine sm = otherGO.getComponent(AnimationStateMachine.class);
            if (sm == null) return false;
            Set<String> otherCoverage = getCoverage(otherGO);
            Set<String> thisCoverage = Window.getScene().worldGrid().getGridCoverage(thisGO.transform);
            boolean overlap = otherCoverage.retainAll(thisCoverage);
            boolean triggered = false;
            if (overlap) {
                GridMachine gm = otherGO.getComponent(GridMachine.class);
                List<SpriteRenderer> sprRenderers = gm.getComponents(SpriteRenderer.class, otherCoverage, Window.getScene().worldGrid());
                triggered = !sprRenderers.isEmpty();
                for (SpriteRenderer spriteRenderer : sprRenderers) {
                    if (spriteRenderer.getCompositeIndex() >= 0) {
                        sm.trigger(trigger, spriteRenderer.getCompositeIndex());
                    } else {
                        sm.trigger(trigger);
                    }
                }
            }
            return triggered;
        }
    }
}
