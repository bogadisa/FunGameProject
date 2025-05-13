package secondEngine.components;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import secondEngine.Component;
import secondEngine.components.helpers.GridState;
import secondEngine.listeners.MouseListener;

public class MouseTracker extends Component {

    @Override
    public void start() {
        
        this.gameObject.transform.position.x = 100;
        this.gameObject.transform.position.y = 100;
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
        // this.gameObject.transform.position.x = MouseListener.getScreenX();
        // this.gameObject.transform.position.y = MouseListener.getScreenY();
    }

}
