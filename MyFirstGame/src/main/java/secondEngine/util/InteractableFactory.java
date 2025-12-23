package secondEngine.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joml.Vector3f;

import secondEngine.Window;
import secondEngine.components.AnimationStateMachine;
import secondEngine.components.Inventory;
import secondEngine.components.Overlay;
import secondEngine.components.SpriteRenderer;
import secondEngine.components.helpers.InventorySlot;
import secondEngine.grid.GridState;
import secondEngine.grid.SpatialGrid;
import secondEngine.objects.GameObject;
import secondEngine.util.InteractableFactory.InteractableIds.Misc;

/**
 * <pre>
 * Categories: <tab/> Misc.
 * 
 * <pre/>
 */
public class InteractableFactory {
    public interface InteractableIds {
        public abstract Categories id();

        enum Categories {
            MISC
        }

        enum Misc implements InteractableIds {
            ENLARGE, HIGHLIGHT, NO_HIGHLIGHT, TOGGLE_HIGHLIGHT, TEMP;

            @Override
            public Categories id() {
                return Categories.MISC;
            }
        }
    }

    private static InteractableFactory interactableFactory;

    private InteractableFactory() {
    }

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
        case TEMP:
            return get().new InteractableFunction() {

                @Override
                public boolean interact(GameObject thisGO, GameObject otherGO) {
                    Inventory thisInventory = thisGO.getComponent(Inventory.class);
                    // Inventory otherInventory = otherGO.getComponent(Inventory.class);
                    Overlay otherOverlay = otherGO.getComponent(Overlay.class);
                    Vector3f thisPos = thisGO.getPosition();
                    Vector3f otherPos = otherGO.getPosition();
                    Vector3f newPos = new Vector3f(thisGO.getPosition()).sub(otherGO.getPosition());
                    List<InventorySlot> slots = otherOverlay.getOverlayGrid()
                            .getObjects(thisInventory.getInventorySlot(0), newPos, InventorySlot.class);
                    InventorySlot otherSlot = slots.get(0);
                    InventorySlot thisSlot = thisInventory.getInventorySlot(0);
                    boolean transfered = otherSlot.transferTo(thisSlot, 1);
                    return true;
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
            GridState gs = go.getGridState(Window.getScene().worldGrid());
            return new HashSet<>(gs.getCurrentGridCells());
        }

        protected boolean trigger(String trigger, GameObject thisGO, GameObject otherGO) {
            AnimationStateMachine sm = otherGO.getComponent(AnimationStateMachine.class);
            if (sm == null) {
                return false;
            }
            // TODO make this part of GridMachine logic
            Set<String> otherCoverage = getCoverage(otherGO);
            // TODO improve scene grid system
            SpatialGrid grid = Window.getScene().worldGrid();
            Set<String> thisCoverage = grid.getGridCoverage(thisGO);
            boolean overlap = otherCoverage.retainAll(thisCoverage);
            if (!overlap) {
                return false;
            }
            GridState otherGS = otherGO.getGridState(grid);
            List<SpriteRenderer> sprRenderers = otherGS.getObjects(SpriteRenderer.class, otherCoverage);
            boolean triggered = !sprRenderers.isEmpty();
            for (SpriteRenderer spriteRenderer : sprRenderers) {
                if (spriteRenderer.getCompositeIndex() >= 0) {
                    sm.trigger(trigger, spriteRenderer.getCompositeIndex());
                } else {
                    sm.trigger(trigger);
                }
            }
            return triggered;
        }
    }
}
