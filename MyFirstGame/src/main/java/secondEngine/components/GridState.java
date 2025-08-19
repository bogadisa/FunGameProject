package secondEngine.components;

import java.util.HashMap;

import secondEngine.Component;
import secondEngine.SpatialGrid;
import secondEngine.Window;

public class GridState extends Component {

    private HashMap<String, String[]> gridCells;
    // TODO is this needed?
    private HashMap<String, String[]> lastGridCells;

    private transient boolean highlightCells = false;

    public GridState init() {
        this.gridCells = new HashMap<>();
        this.lastGridCells = new HashMap<>();
        return this;
    }

    public GridState linkGrid(SpatialGrid grid) {
        gridCells.put(grid.getName(), null);
        return this;
    }

    @Override
    public void start() {
        // if (!isEmpty()) {
        // for (HashMap.Entry<String, SpatialGrid> entry : SpatialGrid.iterateGrids()) {
        // String[] gridCellPos = this.gridCells.get(entry.getKey());
        // if (gridCellPos != null) {
        // entry.getValue().addObject(gameObject);
        // }
        // }

        // }
    }

    @Override
    public void update(float dt) {
        // TODO add dirty flag?
        SpatialGrid worldGrid = Window.getScene().worldGrid();
        String[] lastGridCells = worldGrid.updateObject(this.gameObject);
        this.lastGridCells.put(worldGrid.getName(), lastGridCells);
    }

    public void toggleHighlight() {
        this.highlightCells = !this.highlightCells;
    }

    public boolean highlight() {
        return this.highlightCells;
    }

    public void removeGridCells(String name) {
        this.gridCells.remove(name);
    }

    public void addGridCells(String name, String[] gridCells) {
        this.gridCells.put(name, gridCells);
    }

    public String[] getGridCells(String name) {
        return gridCells.get(name);
    }

    public String[] getLastGridCells(String name) {
        return lastGridCells.get(name);
    }

    public boolean isEmpty() {
        return gridCells.isEmpty();
    }

}
