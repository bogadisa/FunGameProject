package secondEngine.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import secondEngine.Component;
import secondEngine.SpatialGrid;
import secondEngine.Window;
import secondEngine.components.helpers.GridState;

public class GridMachine extends Component {
    private boolean isDirty = false;

    private List<SpatialGrid> grids;
    // TODO is this needed?
    private HashMap<String, GridState> gridStates;

    private transient boolean highlightCells = false;
    private transient Transform lastTransform = new Transform();

    public GridMachine init() {
        this.grids = new ArrayList<>();
        this.gridStates = new HashMap<>();
        return this;
    }

    public GridMachine linkGrid(SpatialGrid grid) {
        grids.add(grid);
        return this;
    }

    @Override
    public void start() {
        linkGrid(Window.getScene().worldGrid());
    }

    @Override
    public void update(float dt) {
        if (!lastTransform.equals(gameObject.transform)) {
            // TODO add update object that can update several spatial grids continuously
            // TODO a bit fucked, need to think more about when an object is updated
            // String[] lastGridCells = null;
            // for (SpatialGrid grid : grids) {
            //     lastGridCells = grid.updateObject(this.gameObject);
            //     this.lastGridCells.put(grid.getName(), lastGridCells);
            // }
            SpatialGrid worldGrid = Window.getScene().worldGrid();
            worldGrid.updateObject(gameObject);
            gameObject.transform.copy(lastTransform);
            isDirty = true;
        } else {
            gameObject.transform.copy(lastTransform);
            isDirty = false;
        }
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

    public void setGridState(String name, GridState gridState) {
        this.gridStates.put(name, gridState);
    }

    public boolean isEmpty() {
        return this.grids.isEmpty();
    }
}
