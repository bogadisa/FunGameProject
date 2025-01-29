package Tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import Utils.FileUtils;
import Utils.ImageUtils;
import main.GamePanel;

public class TileManager {
    GamePanel gp;
    Tile[] tile;

    int mapLayeredTilesTypes[][][];

    String[] imgSrcStrings;

    public TileManager(GamePanel gp) {
        this.gp = gp;

        mapLayeredTilesTypes = new int[1][gp.maxScreenCol][gp.maxScreenRow];

        getTileImages();
    }

    public TileManager(GamePanel gp, int nLayers) {
        // overloaded for multiple layers
        this.gp = gp;

        mapLayeredTilesTypes = new int[nLayers][gp.maxScreenCol][gp.maxScreenRow];

        getTileImages();
    }

    void getTileImages() {
        try {
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
                    tile[i].image = imgsMatrix[i][j];
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load background tiles:");
            System.out.print(e.getStackTrace());
        }
    }

    BufferedImage[] splitSourceImage(BufferedImage src, String imgSrcString) {
        String metadata[] = imgSrcString.split("_");
        metadata[-1] = metadata[-1].substring(0, 1); // removing .png

        int rows = Integer.parseInt(metadata[1]);
        int columns = Integer.parseInt(metadata[2]);
        int nImages = Integer.parseInt(metadata[3]);

        return ImageUtils.getAllSubImages(src, rows, columns, nImages);

    }

    public void loadMaps(String pathToMapFolder) {
        String mapPaths[] = FileUtils.getFiles(pathToMapFolder);
        for (int i = 0; i < mapPaths.length; i++) {
            loadMap(mapPaths[i], i);
        }
    }

    void loadMap(String pathToMap, int layer) {
        try {

            InputStream is = getClass().getResourceAsStream(pathToMap);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
                String line = br.readLine();
                String numbers[] = line.split(" ");
                while (col < gp.maxScreenCol) {
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
            System.out.print(e.getStackTrace());
        }
    }

    public void draw(Graphics2D g2) {
    }

    private void drawLayer(Graphics2D g2, int[][] mapTilesTypes) {
        int col = 0;
        int row = 0;

        int x = 0;
        int y = 0;

        int mapType;

        while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
            mapType = mapTilesTypes[col][row];
            tile[mapType].draw(g2, x, y);
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
