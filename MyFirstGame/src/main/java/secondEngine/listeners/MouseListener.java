package secondEngine.listeners;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import secondEngine.Config.CameraConfig;

public class MouseListener {
    private static MouseListener instance = null;
    private double scrollX, scrollY;
    private double xPos, yPos, lastX, lastY;
    private boolean mouseButtonPressed[] = new boolean[5];
    private boolean mouseButtonStillPressed[] = new boolean[5];
    private boolean isDragging;

    private Vector2f windowPos;

    public interface MouseScrollCallback {
        void callback(double xOffset, double yOffset);
    }
    private List<MouseScrollCallback> mouseScrollCallbacks = new ArrayList<>();

    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    public static MouseListener get() {
        if (MouseListener.instance == null) {
            MouseListener.instance = new MouseListener();
        }

        return MouseListener.instance;
    }

    public static void mousePosCallback(long window, double xpos, double ypos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xpos;
        get().yPos = ypos;
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE) {

            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = false;
                get().mouseButtonStillPressed[button] = false;
                get().isDragging = false; // ?
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
        for (MouseScrollCallback callback : get().mouseScrollCallbacks) {
            callback.callback(xOffset, yOffset);
        }
    }

    public static void registerMouseScrollCallback(MouseScrollCallback callback) {
        get().mouseScrollCallbacks.add(callback);
    }

    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = 0;
        get().lastY = 0;
        // if a button is pressed, and the mouse is moving, it is dragging
        get().isDragging = getIsPressed();
    }

    private static boolean getIsPressed() {
        for (int i = 0; i < get().mouseButtonPressed.length; i++) {
            if (get().mouseButtonPressed[i]) {
                return true;
            }
        }
        return false;
    }

    public static float getX() {
        return (float) get().xPos;
    }

    public static float getY() {
        return (float) get().yPos;
    }

    public static float getScreenX() {
        // float currentX = getX() - get().windowPos.x;
        // float currentX = get().windowPos.x - getX();
        // currentX = (currentX / get().gameViewportSize.x) * 3840.0f;
        // return (float) currentX / CameraConfig.width * CameraConfig.resWidth;
        // return currentX;
        return getX();
    }
    public static float getScreenY() {
        // float currentY = getY() - get().windowPos.y;
        // float currentY =  get().windowPos.y - getY();
        // currentY = (1.0f - (currentY / get().gameViewportSize.y)) * 2160.0f;
        // return (float) (1 - currentY / CameraConfig.height) * CameraConfig.resHeight;
        // return CameraConfig.height - currentY;
        return CameraConfig.height - getY();
    }

    public static float getDx() {
        return (float) (get().lastX - get().xPos);
    }

    public static float getDy() {
        return (float) (get().lastY - get().yPos);  
    }

    public static float getScrollX() {
        return (float) get().scrollX;
    }

    public static float getScrollY() {
        return (float) get().scrollY;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button) {
        // Will it catch everything?
        if (button < get().mouseButtonPressed.length) {
            get().mouseButtonStillPressed[button] = get().mouseButtonPressed[button];
            return get().mouseButtonPressed[button];
        } else {
            return false;
        }
    }

    /**
     * Used when you want one action per press
     */
    public static boolean mouseButtonStillDown(int button) {
        if (button < get().mouseButtonPressed.length) {
            return get().mouseButtonStillPressed[button];
        } else {
            return false;
        }
    }

    public static void setWindowPos(Vector2f pos) {
        get().windowPos = pos;
    }
}
