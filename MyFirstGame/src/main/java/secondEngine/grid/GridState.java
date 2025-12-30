package secondEngine.grid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joml.Vector2f;
import org.joml.Vector4f;

import secondEngine.Camera;
import secondEngine.Window;

public class GridState {
    private class ObjectCell {
        private Griddable object;

        private boolean useScreenCoordinates;

        protected ObjectCell(Griddable object, boolean useScreenCoordinates) {
            this.object = object;
            if (useScreenCoordinates) {
            }
        }

        protected boolean intersects(List<Vector4f> boundriesArray) {
            Camera camera = Window.getScene().camera();
            Vector2f pos = this.object.getPosition().xy(new Vector2f());
            Vector2f scale = this.object.getScale().xy(new Vector2f());
            if (useScreenCoordinates) {
                pos = camera.worldToScreen(pos);
            }
            float xMinA = pos.x;
            float xMaxA = pos.x + scale.x;
            float yMinA = pos.y;
            float yMaxA = pos.y + scale.y;

            float xMinB, xMaxB, yMinB, yMaxB;

            // A.X1 < B.X2: true
            // A.X2 > B.X1: true
            // A.Y1 < B.Y2: true
            // A.Y2 > B.Y1: true
            // Intersect: true
            // else: false
            // TODO misses objects that are bigger than a boundry
            for (Vector4f boundries : boundriesArray) {
                xMinB = boundries.x;
                xMaxB = boundries.y;
                yMinB = boundries.z;
                yMaxB = boundries.w;
                if (xMinA <= xMaxB && xMaxA >= xMinB && yMinA <= yMaxB && yMaxA >= yMinB) {
                    return true;
                }
            }

            return false;
        }
    }

    private SpatialGrid grid;
    private GridableObject object;
    private boolean useScreenCoordinates = false;

    private Set<String> currentGridCells;
    private Set<String> lastGridCells = new HashSet<>();
    private Set<String> differenceGridCells = new HashSet<>();

    private List<ObjectCell> objects = new ArrayList<>();

    public GridState(SpatialGrid grid, GridableObject object) {
        this(new HashSet<>(), grid, object);
    }

    public GridState(Set<String> currentGridCells, SpatialGrid grid, GridableObject object) {
        this.currentGridCells = currentGridCells;
        this.grid = grid;
        this.object = object;
        this.useScreenCoordinates = grid.useScreenCoordinates();
    }

    public void update(Set<String> currentGridCells, Set<String> differenceGridCells) {
        this.lastGridCells = this.currentGridCells;
        this.currentGridCells = currentGridCells;
        this.differenceGridCells = differenceGridCells;
    }

    public void addObject(Griddable object) {
        this.objects.add(new ObjectCell(object, useScreenCoordinates));
    }

    public void remove(Griddable objectToRemove) {
        for (int i = 0; i < this.objects.size(); i++) {
            ObjectCell cell = this.objects.get(i);
            Griddable obj = cell.object;
            if (objectToRemove.getClass().isAssignableFrom(obj.getClass()) && obj == objectToRemove) {
                this.objects.remove(cell);
            }
        }
    }

    public Set<String> getDifferenceGridCells() {
        return differenceGridCells;
    }

    public Set<String> getLastGridCells() {
        return lastGridCells;
    }

    public Set<String> getCurrentGridCells() {
        return currentGridCells;
    }

    private <GridObj extends Griddable> List<ObjectCell> getObjectCells(Class<GridObj> componentClass) {
        List<ObjectCell> objectCells = new ArrayList<>();
        for (ObjectCell cell : this.objects) {
            Griddable obj = cell.object;
            if (obj.isOfType(componentClass)) {
                objectCells.add(cell);
            }
        }
        return objectCells;
    }

    public <GridObj extends Griddable> List<GridObj> getObjects(Class<GridObj> componentClass) {
        List<GridObj> foundObjects = new ArrayList<>();
        for (ObjectCell cell : this.objects) {
            Griddable obj = cell.object;
            if (obj.isOfType(componentClass)) {
                foundObjects.add(componentClass.cast(obj));
            }
        }
        return foundObjects;
    }

    public <GridObj extends Griddable> List<GridObj> getObjects(Class<GridObj> componentClass, Set<String> positions) {
        List<GridObj> foundObjects = new ArrayList<>();
        List<Vector4f> boundriesArray = grid.getBoundriesArray(positions);
        List<ObjectCell> Objectells = getObjectCells(componentClass);
        for (ObjectCell cell : Objectells) {
            if (cell.intersects(boundriesArray)) {
                foundObjects.add(componentClass.cast(cell.object));
            }
        }

        return foundObjects;
    }
}
