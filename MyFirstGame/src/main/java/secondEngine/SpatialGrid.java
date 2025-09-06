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
import secondEngine.components.GridMachine;
import secondEngine.components.Transform;
import secondEngine.objects.GameObject;

public class SpatialGrid {
    private int gridSize = 32;
    // used for when a position lines up exactly with the grid
    private final float secretShrinkageFactor = 0.00001f;

    private String name;
    private static Map<String, SpatialGrid> spatialGrids = new HashMap<>();

    private Map<String, List<GameObject>> objectGrid = new HashMap<>();

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
        return worldToGrid(transform.position);
    }

    public Vector2f gridToWorld(Vector2i pos) {
        return new Vector2f(pos.x * this.gridSize, pos.y * this.gridSize);
    }

    public Vector2f getInternalGridCellOffset(Vector3f pos) {
        Vector2i gridPos = worldToGrid(pos);
        Vector2f worldGridPos = gridToWorld(gridPos);
        // return new Vector2f(pos.x - worldGridPos.x, pos.y - worldGridPos.y);
        return new Vector2f(pos.x - worldGridPos.x, pos.y - worldGridPos.y);
    }

    public Vector2f getInternalGridCellOffset(Transform transform) {
        return getInternalGridCellOffset(transform.position);
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
        Vector2i gridCoords = worldToGrid(transform);
        Vector2f offset = getInternalGridCellOffset(transform);

        float halfWidth = Math.abs(0.5f * transform.scale.x);
        float halfHeight = Math.abs(0.5f * transform.scale.y);

        int leftShift = Math.floorDiv((int) (halfWidth + gridSize - offset.x - secretShrinkageFactor), gridSize);
        int rightShift = Math.floorDiv((int) (halfWidth + offset.x - secretShrinkageFactor), gridSize);

        int upShift = Math.floorDiv((int) (halfHeight + gridSize - offset.y - secretShrinkageFactor), gridSize);
        int downShift = Math.floorDiv((int) (halfHeight + offset.y - secretShrinkageFactor), gridSize);

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

    public List<GameObject> getObjects(Transform transform) {
        String pos = worldToString(transform.position);
        return objectGrid.get(pos);
    }

    // TODO add a method for constraining movement within a cell

    private void addObject(GameObject go, String pos) {
        List<GameObject> gos = this.objectGrid.getOrDefault(pos, new ArrayList<GameObject>());
        gos.add(go);
        this.objectGrid.put(pos, gos);
    }

    private void addObject(GameObject go, String[] coverage) {
        for (String gridCellPos : coverage) {
            addObject(go, gridCellPos);
        }
    }

    public void addObject(GameObject go) {
        String[] coverage = getGridCoverage(go.transform);
        GridMachine gs = go.getComponent(GridMachine.class);
        if (gs == null) {
            gs = new GridMachine().init().linkGrid(this);
            go.addComponent(gs);
        }
        gs.addLastGridCells(name, coverage);
        addObject(go, coverage);
    }

    private void removeObject(GameObject go, String pos) {
        List<GameObject> objs = this.objectGrid.get(pos);
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
            removeObject(go, gridCellPos);
        }

        return coverage;
    }

    public void removeObject(GameObject go) {
        String[] coverage = getGridCoverage(go.transform);
        GridMachine gs = go.getComponent(GridMachine.class);
        if (gs == null) {
            gs = new GridMachine().init();
            go.addComponent(gs);
        }
        gs.removeLastGridCells(name);
        removeObject(go, coverage);
    }

    public String[] updateObject(GameObject go) {
        GridMachine gs = go.getComponent(GridMachine.class);
        String[] prevCoverage = gs.getLastGridCells(name);
        String[] curCoverage = getGridCoverage(go.transform);
        gs.removeLastGridCells(name);
        gs.addLastGridCells(name, curCoverage);

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

    // TODO add update object that can update several spatial grids continuously

    public void setName(String name) {
        this.name = name;
    }
}
