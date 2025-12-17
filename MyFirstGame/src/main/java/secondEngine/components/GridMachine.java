package secondEngine.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.joml.Vector2f;

import secondEngine.Component;
import secondEngine.SpatialGrid;
import secondEngine.Window;
import secondEngine.components.helpers.GridState;

public class GridMachine extends Component {
    private boolean isDirty = false;

    private List<SpatialGrid> grids;
    private HashMap<String, GridState> gridStates;

    private transient boolean highlightCells = false;
    private transient Transform lastTransform = new Transform();

    public GridMachine init() {
        this.grids = new ArrayList<>();
        this.gridStates = new HashMap<>();
        // TODO do we want to assume this?
        linkGrid(Window.getScene().worldGrid());
        return this;
    }

    public GridMachine linkGrid(SpatialGrid grid) {
        if (!grids.contains(grid)) {
            grids.add(grid);
            gridStates.put(grid.getName(), new GridState(grid));
        }
        return this;
    }

    @Override
    public void start() {
    }

    @Override
    public void update(float dt) {
        if (!lastTransform.equals(gameObject.transform)) {
            // TODO add update object that can update several spatial grids continuously
            for (SpatialGrid grid : grids) {
                grid.updateObject(this.gameObject);
            }
            gameObject.transform.copy(lastTransform);
            isDirty = true;
        } else {
            gameObject.transform.copy(lastTransform);
            isDirty = false;
        }
    }

    public void addComponent(SpatialGrid grid, Component component, Vector2f offset, Vector2f size) {
        GridState gs = getGridState(grid.getName());
        gs.addComponent(component, offset, size);
    }

    public <T extends Component> List<T> getComponents(Class<T> componentClass, Set<String> coverage,
            SpatialGrid grid) {
        GridState gs = getGridState(grid.getName());
        if (gs == null) {
            return new ArrayList<>();
        }
        return gs.getComponents(componentClass, coverage);
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public void toggleHighlight() {
        this.highlightCells = !this.highlightCells;
    }

    public boolean highlight() {
        return this.highlightCells;
    }

    public void removeGridState(String name) {
        this.gridStates.remove(name);
    }

    public GridState getGridState(String name) {
        return this.gridStates.get(name);
    }

    public void putGridState(String name, GridState gridState) {
        this.gridStates.put(name, gridState);
    }

    public boolean isEmpty() {
        return this.grids.isEmpty();
    }
}
