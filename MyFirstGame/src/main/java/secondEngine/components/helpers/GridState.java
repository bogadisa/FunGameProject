package secondEngine.components.helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joml.Vector2f;

import secondEngine.Component;
import secondEngine.SpatialGrid;

public class GridState {
    private SpatialGrid grid;

    private Set<String> currentGridCells;
    private Set<String> lastGridCells = new HashSet<>();
    private Set<String> differenceGridCells = new HashSet<>();

    private List<Component> components = new ArrayList<>();

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

    public <T extends Component> List<T> getComponents(Class<T> componentClass) {
        List<T> components = new ArrayList<>();
        for (Component c : this.components) {
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
}
