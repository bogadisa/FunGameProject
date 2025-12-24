package secondEngine.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import secondEngine.Config.CameraConfig;
import secondEngine.objects.GameObject;

public class SpatialGrid {
    private int gridSize = 32;
    // used for when a position lines up exactly with the grid
    private final float secretShrinkageFactor = 0.00001f;

    private String name;
    private boolean useScreenCoordinates = false;
    private static Map<String, SpatialGrid> spatialGrids = new HashMap<>();

    private Map<String, List<Griddable>> objectGrid;

    public SpatialGrid(String name) {
        this(name, false);
    }

    public SpatialGrid(String name, boolean useScreenCoordinates) {
        this.name = name;
        SpatialGrid.spatialGrids.put(name, this);
        this.objectGrid = new HashMap<>();
        this.useScreenCoordinates = useScreenCoordinates;
    }

    public static Set<Entry<String, SpatialGrid>> iterateGrids() {
        return SpatialGrid.spatialGrids.entrySet();
    }

    public boolean useScreenCoordinates() {
        return useScreenCoordinates;
    }

    public int getGridSize() {
        return this.gridSize;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector2f getNearestGrid(Vector3f pos) {
        Vector2f offset = getInternalGridCellOffset(pos);
        return new Vector2f(pos.x - offset.x, pos.y - offset.y);
    }

    public Vector2i toGrid(Vector3f pos) {
        return new Vector2i(Math.floorDiv((int) pos.x, this.gridSize), Math.floorDiv((int) pos.y, this.gridSize));
    }

    public Vector2f fromGrid(Vector2i pos) {
        return new Vector2f(pos.x * this.gridSize, pos.y * this.gridSize);
    }

    public Vector2f getInternalGridCellOffset(Vector3f pos) {
        Vector2i gridPos = toGrid(pos);
        Vector2f acutalPos = fromGrid(gridPos);
        // return new Vector2f(pos.x - acutalPos.x, pos.y - acutalPos.y);
        return new Vector2f(pos.x - acutalPos.x, pos.y - acutalPos.y);
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

    public String encodePos(Vector2i xy) {
        return xy.x + ";;" + xy.y;
    }

    public Vector2f decodePos(String coords) {
        return fromGrid(stringToGrid(coords));
    }

    public String encodePos(Vector3f pos) {
        return encodePos(toGrid(pos));
    }

    public List<Vector4f> getBoundriesArray(Set<String> positions) {
        List<Vector4f> boundriesArray = new ArrayList<>();
        float xMin;
        float xMax;
        float yMin;
        float yMax;
        for (String pos : positions) {
            Vector2f actualPos = decodePos(pos);
            xMin = actualPos.x;
            xMax = actualPos.x + gridSize;
            yMin = actualPos.y;
            yMax = actualPos.y + gridSize;

            boundriesArray.add(new Vector4f(xMin, xMax, yMin, yMax));
        }

        return boundriesArray;
    }

    public <GridObject extends Griddable> Set<String> getGridCoverage(GridObject obj) {
        return getGridCoverage(obj.getPosition(), obj.getScale());
    }

    // TODO doesnt work with single cell objects??
    // secretShrinkageFactor - should fix this
    public Set<String> getGridCoverage(Vector3f pos, Vector3f scale) {
        Vector2i gridCoords = toGrid(pos);
        Vector2f offset = getInternalGridCellOffset(pos);

        float halfWidth = Math.abs(0.5f * scale.x);
        float halfHeight = Math.abs(0.5f * scale.y);

        int leftShift = Math.floorDiv((int) (halfWidth + gridSize - offset.x - secretShrinkageFactor), gridSize);
        int rightShift = Math.floorDiv((int) (halfWidth + offset.x - secretShrinkageFactor), gridSize);

        int upShift = Math.floorDiv((int) (halfHeight + gridSize - offset.y - secretShrinkageFactor), gridSize);
        int downShift = Math.floorDiv((int) (halfHeight + offset.y - secretShrinkageFactor), gridSize);

        Set<String> coverage = new HashSet<>();
        for (int i = -leftShift; i <= rightShift; i++) {
            for (int j = -upShift; j <= downShift; j++) {
                coverage.add((gridCoords.x + i) + ";;" + (gridCoords.y + j));
            }
        }
        return coverage;
    }

    public <GridObject extends Griddable> List<GridObject> getObjects(GridObject obj, Class<GridObject> objClass) {
        return getObjects(obj, obj.getPosition(), objClass);
    }

    public <GridObject extends Griddable> List<GridObject> getObjects(GridObject obj, Vector3f position,
            Class<GridObject> objClass) {
        String pos = encodePos(position);
        List<Griddable> foundObjs = new ArrayList<>(objectGrid.getOrDefault(pos, new ArrayList<>()));
        List<GridObject> filteredObjs = new ArrayList<>();
        for (Griddable foundObj : foundObjs) {
            if (foundObj.isOfType(objClass) && foundObj != obj) {
                filteredObjs.add(objClass.cast(foundObj));
            }
        }
        return filteredObjs;

    }

    // TODO add a method for constraining movement within a cell

    private void addObject(Griddable obj, String pos) {
        List<Griddable> objs = this.objectGrid.getOrDefault(pos, new ArrayList<Griddable>());
        objs.add(obj);
        this.objectGrid.put(pos, objs);
    }

    private void addObject(Griddable obj, Set<String> coverage) {
        for (String gridCellPos : coverage) {
            addObject(obj, gridCellPos);
        }
    }

    public <GridObject extends Griddable> void addObject(GridObject obj) {
        Set<String> coverage = getGridCoverage(obj);
        if (obj.isOfType(GridableObject.class)) {
            GridableObject gridObj = GridableObject.class.cast(obj);
            gridObj.linkGrid(this);
        }
        addObject(obj, coverage);
    }

    private <GridObject extends Griddable> void removeObject(GridObject obj, String pos) {
        List<Griddable> objs = this.objectGrid.get(pos);
        if (objs == null) {
            return;
        }
        for (int i = 0; i < objs.size(); i++) {
            if (obj.getObjectId() == objs.get(i).getObjectId()) {
                objs.remove(i);
                return;
            }
        }
    }

    private <GridObject extends Griddable> void removeObject(GridObject obj, Set<String> coverage) {
        for (String gridCellPos : coverage) {
            removeObject(obj, gridCellPos);
        }
    }

    public <GridObject extends Griddable> void removeObject(GridObject obj) {
        Set<String> coverage = getGridCoverage(obj);
        if (obj.isOfType(GridableObject.class)) {
            GridableObject gridObj = GridableObject.class.cast(obj);
            gridObj.unlinkGrid(this);
        }
        removeObject(obj, coverage);
    }

    public <GridObject extends GridableObject> void updateObject(GridObject gridObj) {
        // GridMachine gm = obj.getComponent(GridMachine.class);
        // GridState gs = gm.getGridState(name);
        GridState gs;
        gs = gridObj.getGridState(this);
        Set<String> diffCells = gs.getCurrentGridCells();
        Set<String> curCoverage = getGridCoverage(gridObj);
        Set<String> curCells = new HashSet<>(curCoverage);

        Set<String> diffCoverage = new HashSet<>(diffCells);
        diffCoverage.retainAll(curCoverage);

        diffCells.removeAll(diffCoverage);
        curCoverage.removeAll(diffCoverage);

        removeObject(gridObj, diffCells);
        addObject(gridObj, curCoverage);

        gs.update(curCells, diffCells);
    }
}
