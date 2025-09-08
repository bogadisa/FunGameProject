package secondEngine.components.helpers;

public class GridState {
    private String[] currentGridCells;
    private String[] lastGridCells = new String[0];
    private String[] differenceGridCells = new String[0];

    public GridState(String[] currentGridCells) {
        this.currentGridCells = currentGridCells;
    }

    public void update(String[] currentGridCells, String[] differenceGridCells) {
        this.lastGridCells = this.currentGridCells;
        this.currentGridCells = currentGridCells;
        this.differenceGridCells = differenceGridCells;
    }

    public String[] getDifferenceGridCells() {
        return differenceGridCells;
    }

    public String[] getLastGridCells() {
        return lastGridCells;
    }

    public String[] getCurrentGridCells() {
        return currentGridCells;
    }
}
