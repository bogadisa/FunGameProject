package secondEngine.components;

import org.joml.Vector3f;

import secondEngine.Component;

public class PlayerControls extends Component{
    float timeSinceLastTrigger = 0.0f;
    boolean runUp = true;
    boolean runDown = true;
    
    float xDirection = 0.0f;
    float yDirection = 0.0f;

    @Override
    public void start() {
        this.gameObject.transform.position.set(new Vector3f(100, 100, 0));
    }

    @Override
    public void update(float dt) {
        StateMachine SM = gameObject.getComponent(StateMachine.class);
        if (timeSinceLastTrigger > 100 && runUp) {
            runUp = false;
            SM.trigger("runUp");
            yDirection = +1.0f;
        } else if (timeSinceLastTrigger > 200 && runDown) {
            SM.trigger("runSideways");
            System.out.println(("21!"));
            yDirection = 0.0f;
            xDirection = 1.0f;
            runDown = false;
        }

        this.gameObject.transform.position.x +=  0.4 * dt * 3 * xDirection;
        this.gameObject.transform.position.y +=  0.4 * dt * 3 * yDirection;
        

        timeSinceLastTrigger += dt;


    }
}