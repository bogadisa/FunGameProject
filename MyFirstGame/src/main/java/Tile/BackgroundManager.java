package Tile;

import Main.GamePanel;

public class BackgroundManager extends TileManager {
    String pathToTileFolder = "resources/tiles/background/";
    String pathToMapsFolder = "resources/maps/background/";

    public BackgroundManager(GamePanel gp) {
        super(gp);
        
        loadRes(pathToTileFolder, pathToMapsFolder);
    }
    public BackgroundManager(GamePanel gp, int nLayers) {
        super(gp, nLayers);
        
        loadRes(pathToTileFolder, pathToMapsFolder);
    }
}