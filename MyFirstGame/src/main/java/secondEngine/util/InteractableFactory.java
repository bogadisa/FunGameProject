package secondEngine.util;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import secondEngine.Window;
import secondEngine.components.AnimationStateMachine;
import secondEngine.components.Inventory;
import secondEngine.components.Overlay;
import secondEngine.components.SpriteRenderer;
import secondEngine.components.TextRenderer;
import secondEngine.components.helpers.InventorySlot;
import secondEngine.grid.GridState;
import secondEngine.grid.SpatialGrid;
import secondEngine.objects.GameObject;
import secondEngine.util.InteractableFactory.InteractableIds.Misc;
import secondEngine.util.InteractableFactory.InteractableIds.MouseInteractables;;

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
            MISC, MOUSE
        }

        enum MouseInteractables implements InteractableIds {
            EXCHANGE_WITH, TRANSFER_TO, READ_POSITION;

            @Override
            public Categories id() {
                return Categories.MOUSE;
            }

        }

        enum Misc implements InteractableIds {
            ENLARGE, HIGHLIGHT, NO_HIGHLIGHT, TOGGLE_HIGHLIGHT,;

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
        case MOUSE:
            func = InteractableFactory.getMouse(interactable);
            break;
        case MISC:
            func = InteractableFactory.getMisc(interactable);
            break;
        default:
            break;
        }
        if (func == null) {
            throw new IllegalArgumentException("Argument of type '" + interactable + "' is not supported");
        }
        return func;
    }

    private static InteractableFunction getMouse(InteractableIds subcategory) {
        switch (MouseInteractables.class.cast(subcategory)) {
        case EXCHANGE_WITH:
            return get().new InteractableFunction() {

                @Override
                public boolean interact(GameObject thisGO, GameObject otherGO) {
                    Inventory thisInventory = thisGO.getComponent(Inventory.class).orElseThrow();
                    Optional<Overlay> optionalOtherOverlay = otherGO.getComponent(Overlay.class);
                    if (optionalOtherOverlay.isEmpty()) {
                        return false;
                    }
                    Overlay otherOverlay = optionalOtherOverlay.get();
                    List<InventorySlot> slots = otherOverlay.getObjects(thisInventory.getInventorySlot(),
                            thisGO.getPosition(), InventorySlot.class);
                    if (slots.isEmpty()) {
                        return false;
                    }
                    InventorySlot otherSlot = slots.get(0);
                    InventorySlot thisSlot = thisInventory.getInventorySlot();
                    return otherSlot.exchangeWith(thisSlot);
                }

            };
        // case TRANSFER_TO:
        // return get().new InteractableFunction() {

        // @Override
        // public boolean interact(GameObject thisGO, GameObject otherGO) {
        // Inventory thisInventory = thisGO.getComponent(Inventory.class);
        // Overlay otherOverlay = otherGO.getComponent(Overlay.class);
        // Vector3f thisPos = thisGO.getPosition();
        // Vector3f otherPos = otherGO.getPosition();
        // Vector3f newPos = new
        // Vector3f(thisGO.getPosition()).sub(otherGO.getPosition());
        // SpatialGrid grid = otherOverlay.getOverlayGrid();
        // List<InventorySlot> slots =
        // grid.getObjects(thisInventory.getInventorySlot(0), newPos,
        // InventorySlot.class);
        // if (slots.size() < 1) {
        // return false;
        // }
        // InventorySlot otherSlot = slots.get(0);
        // InventorySlot thisSlot = thisInventory.getInventorySlot(0);
        // boolean transfered = otherSlot.transferTo(thisSlot, 1);
        // return true;
        // }
        // };
        case READ_POSITION:
            return get().new InteractableFunction() {

                @Override
                public boolean interact(GameObject thisGO, GameObject otherGO) {
                    String pos = otherGO.transform.position.toString();
                    thisGO.getComponent(TextRenderer.class).ifPresent(text -> text.getNamedTextBox("pos").setText(pos));
                    return true;
                }

            };

        default:
            break;
        }
        return null;
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
            GridState gs = go.getGridState(Window.getScene().worldGrid());
            return new HashSet<>(gs.getCurrentGridCells());
        }

        protected boolean trigger(String trigger, GameObject thisGO, GameObject otherGO) {
            Optional<AnimationStateMachine> optionalSM = otherGO.getComponent(AnimationStateMachine.class);
            if (optionalSM.isEmpty()) {
                return false;
            }
            AnimationStateMachine sm = optionalSM.get();
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
