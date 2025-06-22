package secondEngine.components.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import secondEngine.Window;
import secondEngine.Config.UIconfig;
import secondEngine.components.Transform;
import secondEngine.objects.GameObject;

public class OverlayState {
    private static OverlayState overlayState;
    
    private HashMap<Vector2i, List<Interactable>> overlayGrid = new HashMap<>();

    static public OverlayState get() {
        if (OverlayState.overlayState == null) {
            OverlayState.overlayState = new OverlayState();
        }
        return OverlayState.overlayState;
    }

    public static Vector2i screenToGrid(Vector2f pos) {
        return new Vector2i(Math.floorDiv((int)pos.x, UIconfig.getScale()), Math.floorDiv((int)pos.y, UIconfig.getScale()));
    }

    public static Vector2i screenToGrid(Vector3f pos) {
        return screenToGrid(pos.xy(new Vector2f()));
    }

    public static Vector2i worldToGrid(Vector2f world) {
        Vector2f screenPos = Window.getScene().camera().worldToScreen(world);
        return screenToGrid(screenPos);
    }

    public static Vector2i worldToGrid(Vector3f world) {
        return worldToGrid(new Vector2f(world.x, world.y));
    }

    public static Vector2i worldToGrid(Transform transform) {
        return worldToGrid(new Vector2f(
            Math.floorDiv((int)(transform.position.x + 0.5*UIconfig.getScale()), UIconfig.getScale()), 
            Math.floorDiv((int)(transform.position.y + 0.5*UIconfig.getScale()), UIconfig.getScale()))
        );
    }

    public static void addInteractable(Interactable interactable, Vector3f pos) {
        OverlayState.addInteractable(interactable, pos.xy(new Vector2f()));

    }

    public static void addInteractable(Interactable interactable, Vector2f pos) {
        Vector2i gridCoords = OverlayState.worldToGrid(pos);

        List<Interactable> interactables = get().overlayGrid.getOrDefault(gridCoords, new ArrayList<Interactable>());
        interactables.add(interactable);
        get().overlayGrid.put(gridCoords, interactables);
    }

    public static List<Interactable> checkInteractable(Vector2f screen) {
        Vector2i gridCoords = OverlayState.screenToGrid(screen);
        System.out.println(gridCoords);
        List<Interactable> interactables = get().overlayGrid.getOrDefault(gridCoords, null);
        if (interactables == null) return null;
        List<Interactable> activeInteractables = new ArrayList<>();
        for (Interactable interactable : interactables) {
            if (interactable.isActive()) {
                activeInteractables.add(interactable);
            }
        }
        return activeInteractables;
    }

    public static List<Interactable> checkInteractable(Vector3f screen) {
        return OverlayState.checkInteractable(screen.xy(new Vector2f()));
    }
}
