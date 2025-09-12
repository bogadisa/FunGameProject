package secondEngine.components.helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joml.Vector3f;
import org.joml.Vector4f;

import secondEngine.Component;
import secondEngine.SpatialGrid;

public class GridState {
    private class ComponentCell {
        private Component component;
        private Vector3f offset;
        private int size;

        protected ComponentCell(Component component, Vector3f offset, int size) {
            this.component = component;
            this.offset = offset;
            this.size = size;
        }

        protected boolean intersects(List<Vector4f> boundriesArray) {
            float xMin;
            float xMax;
            float yMin;
            float yMax;
            // TODO misses components that are bigger than a boundry
            for (Vector4f boundries : boundriesArray) {
                xMin = boundries.x;
                xMax = boundries.y;
                yMin = boundries.z;
                yMax = boundries.w;
                if ((xMax >= offset.x && offset.x >= xMin) || (xMax >= offset.x + size && offset.x + size >= xMin)) {
                    return true;
                } else if ((yMax >= offset.y && offset.y >= yMin)
                        || (yMax >= offset.y + size && offset.y + size >= yMin)) {
                    return true;
                }
            }

            return false;
        }
    }

    private SpatialGrid grid;

    private Set<String> currentGridCells;
    private Set<String> lastGridCells = new HashSet<>();
    private Set<String> differenceGridCells = new HashSet<>();

    private List<ComponentCell> components = new ArrayList<>();

    public GridState(Set<String> currentGridCells, SpatialGrid grid) {
        this.currentGridCells = currentGridCells;
        this.grid = grid;
    }

    public void update(Set<String> currentGridCells, Set<String> differenceGridCells) {
        this.lastGridCells = this.currentGridCells;
        this.currentGridCells = currentGridCells;
        this.differenceGridCells = differenceGridCells;
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

    private <T extends Component> List<ComponentCell> getComponentCells(Class<T> componentClass) {
        List<ComponentCell> componentCells = new ArrayList<>();
        for (ComponentCell cell : this.components) {
            Component c = cell.component;
            if (componentClass.isAssignableFrom(c.getClass())) {
                componentCells.add(cell);
            }
        }
        return componentCells;
    }

    public <T extends Component> List<T> getComponents(Class<T> componentClass) {
        List<T> components = new ArrayList<>();
        for (ComponentCell cell : this.components) {
            Component c = cell.component;
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    components.add(componentClass.cast(c));
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error: Casting component.";
                }
            }
        }
        return components;
    }

    public <T extends Component> List<T> getComponents(Class<T> componentClass, Set<String> positions) {
        List<T> components = new ArrayList<>();
        List<Vector4f> boundriesArray = grid.getBoundriesArray(positions);
        List<ComponentCell> componentCells = getComponentCells(componentClass);
        for (ComponentCell cell : componentCells) {
            if (cell.intersects(boundriesArray)) {
                components.add(componentClass.cast(cell.component));
            }
        }

        return components;
    }
}
