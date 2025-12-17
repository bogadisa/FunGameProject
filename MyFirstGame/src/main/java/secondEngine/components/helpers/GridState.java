package secondEngine.components.helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joml.Vector2f;
import org.joml.Vector4f;

import secondEngine.Component;
import secondEngine.SpatialGrid;

public class GridState {
    private class ComponentCell {
        private Component component;
        private Vector2f offset;
        private Vector2f scale;

        protected ComponentCell(Component component, Vector2f offset, Vector2f scale) {
            this.component = component;
            this.offset = offset;
            this.scale = scale;
        }

        protected boolean intersects(List<Vector4f> boundriesArray) {
            float xMinA = component.gameObject.transform.position.x + offset.x;
            float xMaxA = component.gameObject.transform.position.x + offset.x + scale.x;
            float yMinA = component.gameObject.transform.position.y + offset.y;
            float yMaxA = component.gameObject.transform.position.y + offset.y + scale.y;

            float xMinB, xMaxB, yMinB, yMaxB;

            // A.X1 < B.X2: true
            // A.X2 > B.X1: true
            // A.Y1 < B.Y2: true
            // A.Y2 > B.Y1: true
            // Intersect: true
            // else: false
            // TODO misses components that are bigger than a boundry
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

    private Set<String> currentGridCells;
    private Set<String> lastGridCells = new HashSet<>();
    private Set<String> differenceGridCells = new HashSet<>();

    private List<ComponentCell> components = new ArrayList<>();

    public GridState(SpatialGrid grid) {
        this(new HashSet<>(), grid);
    }

    public GridState(Set<String> currentGridCells, SpatialGrid grid) {
        this.currentGridCells = currentGridCells;
        this.grid = grid;
    }

    public void update(Set<String> currentGridCells, Set<String> differenceGridCells) {
        this.lastGridCells = this.currentGridCells;
        this.currentGridCells = currentGridCells;
        this.differenceGridCells = differenceGridCells;
    }

    public void addComponent(Component component, Vector2f offset, Vector2f scale) {
        this.components.add(new ComponentCell(component, offset, scale));
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
