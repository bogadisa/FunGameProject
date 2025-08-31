package secondEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import secondEngine.Config.CameraConfig;
import secondEngine.components.GridState;
import secondEngine.components.Transform;
import secondEngine.objects.GameObject;

public class SpatialGrid {
    private int gridSize = 32;

    private String name;
    private static Map<String, SpatialGrid> spatialGrids = new HashMap<>();

    private Map<Transform, List<GameObject>> objectGrid = new HashMap<>();

    public SpatialGrid(String name) {
        this.name = name;
        SpatialGrid.spatialGrids.put(name, this);
        this.objectGrid = new HashMap<>();
    }

    public static Set<Entry<String, SpatialGrid>> iterateGrids() {
        return SpatialGrid.spatialGrids.entrySet();
    }

    public int getGridSize() {
        return this.gridSize;
    }

    public String getName() {
        return this.name;
    }

    public Vector2f getNearestGrid(Vector3f pos) {
        Vector2f offset = getInternalGridCellOffset(pos);
        return new Vector2f(pos.x - offset.x, pos.y - offset.y);
    }

    public Vector2i worldToGrid(Vector3f pos) {
        return new Vector2i(Math.floorDiv((int) pos.x, this.gridSize), Math.floorDiv((int) pos.y, this.gridSize));
    }

    public Vector2i worldToGrid(Transform transform) {
        // TODO what is right? prolly not
        return new Vector2i(Math.floorDiv((int) (transform.position.x), this.gridSize),
                Math.floorDiv((int) (transform.position.y), this.gridSize));
        // return new Vector2i(Math.floorDiv((int) (transform.position.x + 0.5 *
        // this.gridSize), this.gridSize),
        // Math.floorDiv((int) (transform.position.y + 0.5 * this.gridSize),
        // this.gridSize));
    }

    public Vector2f gridToWorld(Vector2i pos) {
        // TODO what is right? prolly not
        // return new Vector2f(pos.x * this.gridSize + (int) (0.5 * this.gridSize),
        // pos.y * this.gridSize + (int) (0.5 * this.gridSize));

        return new Vector2f(pos.x * this.gridSize, pos.y * this.gridSize);
    }

    public Vector2f getInternalGridCellOffset(Vector3f pos) {
        Vector2i gridPos = worldToGrid(pos);
        Vector2f worldGridPos = gridToWorld(gridPos);
        // return new Vector2f(pos.x - worldGridPos.x, pos.y - worldGridPos.y);
        return new Vector2f(pos.x - worldGridPos.x, pos.y - worldGridPos.y);
    }

    public Vector2i getGridScale() {
        int gridCellsX = Math.floorDiv(CameraConfig.width, this.gridSize) + 1;
        int gridCellsY = Math.floorDiv(CameraConfig.height, this.gridSize) + 1;
        return new Vector2i(gridCellsX, gridCellsY);
    }

    public Vector2i stringToGrid(String coords) {
        String[] xy = coords.split(";;");
        int x = Integer.parseInt(xy[0]);
        int y = Integer.parseInt(xy[1]);
        return new Vector2i(x, y);
    }

    public String gridToString(Vector2i xy) {
        return xy.x + ";;" + xy.y;
    }

    public Vector2f stringToWorld(String coords) {
        return gridToWorld(stringToGrid(coords));
    }

    public String worldToString(Vector3f pos) {
        return gridToString(worldToGrid(pos));
    }

    private String[] getGridCoverage(Transform transform) {
        Vector2i gridCoords = worldToGrid(transform.position);
        Vector2f offset = getInternalGridCellOffset(transform.position);

        float halfWidth = Math.abs(0.5f * transform.scale.x);
        float halfHeight = Math.abs(0.5f * transform.scale.y);

        int leftShift = Math.floorDiv((int) (gridSize - offset.x + halfWidth), gridSize);
        int rightShift = Math.floorDiv((int) (halfWidth + offset.x), gridSize);

        int upShift = Math.floorDiv((int) (gridSize - offset.y + halfHeight), gridSize);
        int downShift = Math.floorDiv((int) (halfHeight + offset.y), gridSize);

        String[] coverage = new String[(leftShift + rightShift + 1) * (upShift + downShift + 1)];
        int k = 0;
        for (int i = -leftShift; i <= rightShift; i++) {
            for (int j = -upShift; j <= downShift; j++) {
                coverage[k] = (gridCoords.x + i) + ";;" + (gridCoords.y + j);
                k++;
            }
        }
        return coverage;
    }

    // TODO add a method for constraining movement within a cell

    private void addObject(GameObject go, Transform transform) {
        List<GameObject> gos = this.objectGrid.getOrDefault(transform, new ArrayList<GameObject>());
        gos.add(go);
        this.objectGrid.put(transform, gos);
    }

    private void addObject(GameObject go, String[] coverage) {
        for (String gridCellPos : coverage) {
            Vector2i xy = stringToGrid(gridCellPos);
            addObject(go, new Transform().init(new Vector3f(new Vector3f(xy.x, xy.y, 0))));
        }
    }

    public void addObject(GameObject go) {
        String[] coverage = getGridCoverage(go.transform);
        GridState gs = go.getComponent(GridState.class);
        if (gs == null) {
            gs = new GridState().init().linkGrid(this);
            go.addComponent(gs);
        }
        gs.addGridCells(name, coverage);
        addObject(go, coverage);
    }

    private void removeObject(GameObject go, Transform transform) {
        List<GameObject> objs = this.objectGrid.get(transform);
        if (objs != null) {
            for (int i = 0; i < objs.size(); i++) {
                if (go.getObjectId() == objs.get(i).getObjectId()) {
                    objs.remove(i);
                    break;
                }
            }
        }
    }

    private String[] removeObject(GameObject go, String[] coverage) {
        for (String gridCellPos : coverage) {
            Vector2i xy = stringToGrid(gridCellPos);
            removeObject(go, new Transform().init(new Vector3f(xy.x, xy.y, 0)));
        }

        return coverage;
    }

    public void removeObject(GameObject go) {
        String[] coverage = getGridCoverage(go.transform);
        GridState gs = go.getComponent(GridState.class);
        if (gs == null) {
            gs = new GridState().init();
            go.addComponent(gs);
        }
        gs.removeGridCells(name);
        removeObject(go, coverage);
        ;
    }

    public String[] updateObject(GameObject go) {
        GridState gs = go.getComponent(GridState.class);
        String[] prevCoverage = gs.getGridCells(name);
        String[] curCoverage = getGridCoverage(go.transform);
        gs.removeGridCells(name);
        gs.addGridCells(name, curCoverage);

        // TODO might be slow
        Set<String> removeCoverage = new HashSet<>(Arrays.asList(prevCoverage));
        Set<String> intersectCoverage = new HashSet<>(removeCoverage);
        Set<String> keepCoverage = new HashSet<>(Arrays.asList(curCoverage));

        intersectCoverage.retainAll(keepCoverage);
        removeCoverage.removeAll(intersectCoverage);
        keepCoverage.removeAll(intersectCoverage);

        String[] lastCells = removeObject(go, removeCoverage.toArray(new String[removeCoverage.size()]));
        addObject(go, keepCoverage.toArray(new String[keepCoverage.size()]));
        return lastCells;

    }

    public void setName(String name) {
        this.name = name;
    }
}
