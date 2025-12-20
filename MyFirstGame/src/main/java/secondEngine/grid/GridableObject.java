package secondEngine.grid;

import java.util.HashMap;
import java.util.Map;

public abstract class GridableObject implements Griddable {
    public Map<SpatialGrid, GridState> gridStates = new HashMap<>();

    public void linkGrid(SpatialGrid grid) {
        if (gridStates.containsKey(grid)) {
            return;
        }
        GridState gs = new GridState(grid, this);
        gridStates.put(grid, gs);

    }

    public void unlinkGrid(SpatialGrid grid) {
        if (!gridStates.containsKey(grid)) {
            return;
        }
        // TODO possible memory leak? should I manually delete the grid state?
        gridStates.remove(grid);
    }

    public GridState getGridState(SpatialGrid grid) {
        return gridStates.get(grid);
    }

}
