package secondEngine.components;

import static org.lwjgl.glfw.GLFW.*;

import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import secondEngine.Component;
import secondEngine.Window;
import secondEngine.components.helpers.GridState;
import secondEngine.components.helpers.Interactable;
import secondEngine.components.helpers.OverlayState;
import secondEngine.listeners.MouseListener;
import secondEngine.objects.GameObject;
import secondEngine.util.PrefabFactory;

public class MouseTracker extends Component {
    private transient Transform lastTransform = new Transform();

    // temp
    private int currentSprite = 0;

    private static boolean hasScrolledY = false;
    private boolean hasScrolledLocal = false;

    @Override
    public void start() {
        
        this.gameObject.transform.position.x = 100;
        this.gameObject.transform.position.y = 100;
        this.gameObject.transform.copy(lastTransform);
        MouseListener.registerMouseScrollCallback((double xOffset, double yOffset) -> onScrollCallback(xOffset, yOffset));
    }

    @Override
    public void update(float dt) {
        Vector3f mouseCoords =  new Vector3f(MouseListener.getScreenX(), MouseListener.getScreenY(), 0);
        this.gameObject.transform.position.x = mouseCoords.x;
        this.gameObject.transform.position.y = mouseCoords.y;
        // Vector2i gridCoords = GridState.worldToGrid(this.gameObject.transform);
        Vector2i gridCoords = GridState.worldToGrid(mouseCoords);
        Vector2f worldCoords = GridState.gridToWorld(gridCoords);

        if (this.gameObject.transform.gridLockX) {
            this.gameObject.transform.position.x = worldCoords.x;
        }
        if (this.gameObject.transform.gridLockY) {
            this.gameObject.transform.position.y = worldCoords.y;
        }
        
        if (!MouseListener.mouseButtonStillDown(GLFW_MOUSE_BUTTON_2)) {
            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_2)) {
                
                // SpriteRenderer sprite = this.gameObject.getComponent(SpriteRenderer.class);
                // sprite.setHidden(!sprite.isHidden());
                List<Interactable> interactables = OverlayState.checkInteractable(mouseCoords);
                if (interactables != null) {
                    for (Interactable interactable : interactables) {
                        interactable.interact();
                    }
                }
            }
        }
        if (MouseTracker.hasScrolledY) {
            if (!hasScrolledLocal) {
                this.currentSprite += MouseListener.getScrollY();
                if (this.currentSprite < 0) {
                    this.currentSprite = 14;
                } else if (this.currentSprite > 14) {
                    this.currentSprite = 0;
                }
                // this.gameObject.getComponent(SpriteRenderer.class).setSprite(PrefabFactory.getObjectSprite(1000+currentSprite));
            } else {
                MouseTracker.hasScrolledY = false;
            }
            hasScrolledLocal = true;
        } else {
            hasScrolledLocal = false;
        }
        if (!MouseListener.mouseButtonStillDown(GLFW_MOUSE_BUTTON_1) || !this.lastTransform.equals(gameObject.transform)) {
            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1)) {
                GameObject go = PrefabFactory.getObject(1000 + currentSprite);
                go.transform.position.set(this.gameObject.transform.position);
                Window.getScene().addGameObjectToScene(go);
            }
        }
        if (!this.lastTransform.equals(gameObject.transform) ) {
            gameObject.transform.copy(lastTransform);
        }
    }

    public static void onScrollCallback(double xOffset, double yOffset) {
        if (yOffset != 0) {
            MouseTracker.hasScrolledY = true;
        }
    }

}
