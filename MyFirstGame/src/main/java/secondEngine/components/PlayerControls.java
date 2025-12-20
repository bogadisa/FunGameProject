package secondEngine.components;

import static org.lwjgl.glfw.GLFW.*;

import java.io.IOException;

import secondEngine.Component;
import secondEngine.Window;
import secondEngine.grid.SpatialGrid;
import secondEngine.listeners.KeyListener;

public class PlayerControls extends Component {

    private float playerWidth = 16.0f;
    private float xSpeed = 0.0f;
    private float ySpeed = 0.0f;

    private transient AnimationStateMachine stateMachine;

    public void setPlayerWidth(float playerWidth) {
        this.playerWidth = playerWidth;
    }

    @Override
    public void start() {
        // this.gameObject.transform.position.set(new Vector3f(100, 100, 0));
        this.stateMachine = this.gameObject.getComponent(AnimationStateMachine.class);
    }

    @Override
    public void update(float dt) {
        float xDirection = 0.0f;
        float yDirection = 0.0f;
        if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
            xDirection = 1.0f;
            this.stateMachine.trigger("runSideways");
            this.gameObject.transform.scale.x = this.playerWidth;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
            xDirection = -1.0f;
            this.stateMachine.trigger("runSideways");
            this.gameObject.transform.scale.x = -this.playerWidth;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
            yDirection = 1.0f;
            this.stateMachine.trigger("runUp");
        } else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
            yDirection = -1.0f;
            this.stateMachine.trigger("runDown");
        } else if (KeyListener.isKeyPressed(GLFW_KEY_C)) {
            try {
                try {
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            this.stateMachine.trigger("idle");
        }

        // if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
        // KeyListener.isKeyPressed(GLFW_KEY_T)) {
        // this.gameObject.getComponent(GridState.class).toggleHighlight();
        // System.out.println("Toggled!");
        // }

        xSpeed = 3.3f * dt * 5 * xDirection;
        ySpeed = 3.3f * dt * 5 * yDirection;
        // this.gameObject.transform.position.x += xSpeed;
        // this.gameObject.transform.position.y += ySpeed;
        this.gameObject.transform.position.add(xSpeed, ySpeed, 0);
        SpatialGrid grid = Window.getScene().worldGrid();
        grid.updateObject(this.gameObject);
    }
}