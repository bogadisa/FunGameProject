package Tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import Main.GamePanel;
import Utils.FileUtils;
import Utils.ImageUtils;

public class TileManager {
    GamePanel gp;
    Tile[] tile;

    int mapLayeredTilesTypes[][][];
    int nLayers;

    public String pathToTileFolder;
    public String pathToMapsFolder;

    protected TileManager(GamePanel gp) {
        this.gp = gp;
        this.nLayers = 1;

        mapLayeredTilesTypes = new int[nLayers][gp.maxScreenCol][gp.maxScreenRow];
    }

    protected TileManager(GamePanel gp, int nLayers) {
        // overloaded for multiple layers
        this.gp = gp;
        this.nLayers = nLayers;

        mapLayeredTilesTypes = new int[nLayers][gp.maxScreenCol][gp.maxScreenRow];
    }

    protected void loadRes(String pathToTileFolder, String pathToMapsFolder) {
            getTileImages(pathToTileFolder);
            loadMaps(pathToMapsFolder);
            // for (int i = 0; i < tile.length; i++) {
            //     System.out.println(tile[i].name);
            // }
            System.exit(0);
        }

    private void getTileImages(String pathToTileFolder) {
        try {
            String imgSrcStrings[] = FileUtils.getFiles(pathToTileFolder);

            int nImages = 0;
            BufferedImage imgsMatrix[][] = new BufferedImage[imgSrcStrings.length][];
            for (int i = 0; i < imgSrcStrings.length; i++) {
                File f = new File(imgSrcStrings[i]);
                BufferedImage src = ImageIO.read(f);

                imgsMatrix[i] = splitSourceImage(src, imgSrcStrings[i]);
                nImages += imgsMatrix[i].length;
            }
            tile = new Tile[nImages];
            for (int i = 0; i < imgsMatrix.length; i++) {
                for (int j = 0; j < imgsMatrix[i].length; j++) {
                    tile[i] = new Tile();
                    tile[i].image = imgsMatrix[i][j];
                    tile[i].name = i;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load background tiles:");
            e.printStackTrace();
        }
    }

    private BufferedImage[] splitSourceImage(BufferedImage src, String imgSrcString) {
        String metadata[] = imgSrcString.split("_");
        metadata[3] = metadata[3].substring(0, 1); // removing .png

        int rows = Integer.parseInt(metadata[1]);
        int columns = Integer.parseInt(metadata[2]);
        int nImages = Integer.parseInt(metadata[3]);

        return ImageUtils.getAllSubImages(src, rows, columns, nImages);

    }

    public void loadMaps(String pathToMapFolder) {
        String mapPaths[] = FileUtils.getFiles(pathToMapFolder);
        for (int i = 0; i < mapPaths.length; i++) {
            System.out.println(mapPaths[i]);
            loadMap(mapPaths[i], i);
        }
    }

    private void loadMap(String pathToMap, int layer) {
        try {

            InputStream is = getClass().getResourceAsStream(pathToMap);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
                String line = br.readLine();
                String numbers[] = line.split(" ");
                while (col < gp.maxScreenCol) {
                    //  + layer != 0 ? 1 : 0 * 
                    int tileType = Integer.parseInt(numbers[col]);
                    mapLayeredTilesTypes[layer][col][row] = tileType;
                    col++;
                }
                col = 0;
                row++;
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Failed to load map:");
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        for (int i = 0; i < nLayers; i++) {
            drawLayer(g2, mapLayeredTilesTypes[i]);
        }
    }

    private void drawLayer(Graphics2D g2, int[][] mapTilesTypes) {
        int col = 0;
        int row = 0;

        int x = 0;
        int y = 0;

        int mapTileType;

        while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
            mapTileType = mapTilesTypes[col][row];
            tile[mapTileType].draw(g2, x, y);
            col++;
            x += gp.tileSize;
            if (col == gp.maxScreenCol) {
                col = 0;
                x = 0;
                row++;
                y += gp.tileSize;
            }
        }

    }
}
