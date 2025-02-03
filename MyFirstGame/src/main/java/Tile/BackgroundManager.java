package Tile;

import java.awt.image.BufferedImage;

import Main.GamePanel;

public class BackgroundManager extends TileManager {
    String pathToTileFolder = "background/";
    String pathToMapsFolder = "../maps/background/";

    public BackgroundManager(GamePanel gp) {
        super(gp);

        loadRes(pathToTileFolder, pathToMapsFolder);
    }

    public BackgroundManager(GamePanel gp, int nLayers) {
        super(gp, nLayers);

        loadRes(pathToTileFolder, pathToMapsFolder);
    }

    @Override
    public int convertImgsToTile(BufferedImage[] imgs, int tileIndex) {
        Tile grassTile = new Tile();
        grassTile.addImgs(imgs, 0, 4);
        grassTile.colision = true;
        tiles.put("C-" + tileIndex, grassTile);

        Tile groundTile = new Tile();
        groundTile.addImgs(imgs, 4, 10);
        groundTile.colision = true;
        tiles.put("C-" + (tileIndex + 1), groundTile);

        Tile rockTile = new Tile();
        rockTile.addImgs(imgs, 10, 11);
        rockTile.colision = false;
        tiles.put("C-" + (tileIndex + 2), rockTile);

        Tile shrubTile = new Tile();
        shrubTile.addImgs(imgs, 11, 15);
        shrubTile.colision = false;
        tiles.put("C-" + (tileIndex + 3), shrubTile);

        return tileIndex + 4;
    }

}