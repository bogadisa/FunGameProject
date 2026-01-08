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

public class SpatialGrid {
    private Vector2f gridOffset = new Vector2f(0);
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

    public void setOffset(Vector2f offset) {
        this.gridOffset = offset;
    }

    public void setOffset(float x, float y) {
        this.gridOffset = new Vector2f(x, y);
    }

    public Vector2f getOffset() {
        return this.gridOffset;
    }

    public void setGridSize(int gridsize) {
        this.gridSize = gridsize;
    }

    public int getGridSize() {
        return this.gridSize;
    }

    public static Set<Entry<String, SpatialGrid>> iterateGrids() {
        return SpatialGrid.spatialGrids.entrySet();
    }

    public boolean useScreenCoordinates() {
        return useScreenCoordinates;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Vector2f getRelativePos(Vector3f pos) {
        return getRelativePos(pos.xy(new Vector2f()));
    }

    private Vector2f getRelativePos(Vector2f pos) {
        return new Vector2f(pos).add(gridOffset);
    }

    public Vector2f getNearestGrid(Vector3f pos) {
        Vector2f relativePos = getRelativePos(pos);
        Vector2f offset = getInternalGridCellOffset(pos);
        return new Vector2f(relativePos.x - offset.x, relativePos.y - offset.y);
    }

    public Vector2i toGrid(Vector3f pos) {
        return toGrid(pos, true);
    }

    public Vector2i toGrid(Vector3f pos, boolean useOffset) {
        Vector2f relativePos = getRelativePos(pos);
        return new Vector2i(Math.floorDiv((int) relativePos.x, this.gridSize),
                Math.floorDiv((int) relativePos.y, this.gridSize));
    }

    public Vector2f fromGrid(Vector2i pos) {
        Vector2f otherPos = new Vector2f(pos.x * this.gridSize, pos.y * this.gridSize);
        return otherPos;
    }

    public Vector2f getInternalGridCellOffset(Vector3f pos) {
        Vector2i gridPos = toGrid(pos);
        Vector2f relativePos = getRelativePos(pos);
        Vector2f gridDecoded = fromGrid(gridPos);

        return relativePos.sub(gridDecoded);
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

    public String encodePos(Vector3f pos) {
        return encodePos(toGrid(pos));
    }

    public String encodePos(Vector2i xy) {
        return xy.x + ";;" + xy.y;
    }

    public Vector2f decodePos(String coords) {
        return fromGrid(stringToGrid(coords));
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
                coverage.add(encodePos(new Vector2i(gridCoords).add(i, j)));
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
