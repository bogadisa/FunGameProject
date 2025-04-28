package secondEngine.components;

import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

import secondEngine.Component;
import secondEngine.listeners.KeyListener;

public class PlayerControls extends Component{

    private transient float playerWidth;
    

    private transient StateMachine stateMachine;

    @Override
    public void start() {
        this.gameObject.transform.position.set(new Vector3f(100, 100, 0));
        this.stateMachine = this.gameObject.getComponent(StateMachine.class);

        this.playerWidth = this.gameObject.transform.scale.x;
    }

    @Override
    public void update(float dt) {
        float xDirection = 0.0f;
        float yDirection = 0.0f;
        if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
            xDirection = 1.0f;
            this.stateMachine.trigger("runSideways");
            if (this.gameObject.transform.scale.x < 0) {
                this.gameObject.transform.scale.x = this.playerWidth;
                this.gameObject.transform.position.x -= this.playerWidth;
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
            xDirection = -1.0f;
            this.stateMachine.trigger("runSideways");
            if (this.gameObject.transform.scale.x > 0) {
                this.gameObject.transform.scale.x = -this.playerWidth;
                this.gameObject.transform.position.x += this.playerWidth;
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
            yDirection = 1.0f;
            this.stateMachine.trigger("runUp");
        } else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
            yDirection = -1.0f;
            this.stateMachine.trigger("runDown");
        } else {
            this.stateMachine.trigger("idle");
        }

        this.gameObject.transform.position.x +=  0.4 * dt * 5 * xDirection;
        this.gameObject.transform.position.y +=  0.4 * dt * 5 * yDirection;
    }
}