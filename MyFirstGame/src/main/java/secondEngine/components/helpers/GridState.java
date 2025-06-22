package secondEngine.components.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import secondEngine.Config.CameraConfig;
import secondEngine.components.Transform;
import secondEngine.objects.GameObject;

public class GridState {
    private static GridState gridState;

    private int gridSize = 32;

    private HashMap<Transform, List<GameObject>> objectGrid = new HashMap<>();

    static public GridState get() {
        if (GridState.gridState == null) {
            GridState.gridState = new GridState();
        }
        return GridState.gridState;
    }

    public static int getGridSize() {
        return get().gridSize;
    }

    public static Vector2i worldToGrid(Vector3f pos) {
        return new Vector2i(Math.floorDiv((int)pos.x, get().gridSize), Math.floorDiv((int)pos.y, get().gridSize));
    }

    public static Vector2i worldToGrid(Transform transform) {
        // TODO what is right? prolly not
        // return new Vector2i(
        //     Math.floorDiv((int)(transform.position.x + 0.5*transform.scale.x), get().gridSize), 
        //     Math.floorDiv((int)(transform.position.y + 0.5*transform.scale.y), get().gridSize)
        //     );
        return new Vector2i(
        Math.floorDiv((int)(transform.position.x + 0.5*get().gridSize), get().gridSize), 
        Math.floorDiv((int)(transform.position.y + 0.5*get().gridSize), get().gridSize)
        );
    }

    public static Vector2f gridToWorld(Vector2i pos) {
        // TODO what is right? prolly not
        return new Vector2f(pos.x*get().gridSize + (int)(0.5*get().gridSize), pos.y*get().gridSize + (int)(0.5*get().gridSize));
    }

    public static Vector2f getInternalGridCellOffset(Vector3f pos) {
        Vector2i gridPos = GridState.worldToGrid(pos);
        return new Vector2f(pos.x - gridPos.x, pos.y - gridPos.y);
    }

    public static Vector2i getGridScale() {
        int gridCellsX = Math.floorDiv(CameraConfig.width, get().gridSize) + 1;
        int gridCellsY = Math.floorDiv(CameraConfig.height, get().gridSize) + 1;
        return new Vector2i(gridCellsX, gridCellsY);
    }

    // TODO add a method for constraining movement within a cell

    private static void addObject(GameObject go, Transform transform) {
        List<GameObject> gos = get().objectGrid.getOrDefault(transform, new ArrayList<GameObject>());
        gos.add(go);
        get().objectGrid.put(transform, gos);
    }

    public static void addObject(GameObject go) {
        // Position holds the center coordinate of the object
        // meaning we have to dynamically find which surrounding grid cells
        // should be considered.
        Vector2i gridCoords = GridState.worldToGrid(go.transform.position);
        Vector2f offset = GridState.getInternalGridCellOffset(go.transform.position);
        int gridSize = get().gridSize;
        int gridWidth = Math.floorDiv((int)go.transform.scale.x, gridSize);
        int gridHeight = Math.floorDiv((int)go.transform.scale.y, gridSize);

        // if gridWidth/gridHeight is an odd number, we add (gridWidth-1)/2 cells on each side
        // otherwise, we check the grid coord of the left/bottom side to see how many cells away it is
        int gridCellsOnEachSide;
        int rightShift = 0;
        int leftShift = 0;
        if (gridWidth % 2 == 1) {
            // odd number
            gridCellsOnEachSide = (gridWidth - 1)/2;
            
        } else {
            // even number
            if (offset.x > (gridSize / 2)) {
                rightShift = 1;
            } else {
                leftShift = 1;
            }

            gridCellsOnEachSide = gridWidth/2;
        }
        for (int i = -gridCellsOnEachSide+rightShift; i <= gridCellsOnEachSide-leftShift; i++) {
            GridState.addObject(go, new Transform().init(new Vector3f(gridCoords.x + i, gridCoords.y, 0)));
        }

        int upshift = 0;
        int downshift = 0;
        if (gridHeight % 2 == 1) {
            // odd number
            gridCellsOnEachSide = (gridHeight - 1)/2;
            
        } else {
            // even number
            if (offset.y > (gridSize / 2)) {
                upshift = 1;
            } else {
                downshift = 1;
            }

            gridCellsOnEachSide = gridHeight/2;
        }
        for (int i = -gridCellsOnEachSide+upshift; i <= gridCellsOnEachSide-downshift; i++) {
            GridState.addObject(go, new Transform().init(new Vector3f(gridCoords.x, gridCoords.y + i, 0)));
        }

    }
}
