package Tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;

import javax.imageio.ImageIO;

import Main.GamePanel;
import Utils.ImageUtils;

public class TileManager {
    GamePanel gp;
    public HashMap<String, Tile> tiles = new HashMap<String, Tile>();

    public String mapLayeredTilesTypes[][][];
    public String currentMap[][];
    public int nLayers;

    public String pathToTileFolder;
    public String pathToMapsFolder;

    protected TileUtil tileUtil;

    protected TileManager(GamePanel gp) {
        this.gp = gp;
        this.nLayers = 1;

        mapLayeredTilesTypes = new String[nLayers][gp.maxScreenCol][gp.maxScreenRow];
        currentMap = new String[gp.maxScreenCol][gp.maxScreenRow];

        tiles = new HashMap<String, Tile>();
        tileUtil = new TileUtil(tiles);
    }

    protected TileManager(GamePanel gp, int nLayers) {
        // overloaded for multiple layers
        this.gp = gp;
        this.nLayers = nLayers;

        mapLayeredTilesTypes = new String[nLayers][gp.maxScreenCol][gp.maxScreenRow];
        currentMap = new String[gp.maxScreenCol][gp.maxScreenRow];

        tiles = new HashMap<String, Tile>();
        tileUtil = new TileUtil(tiles);
    }

    protected void loadRes(String pathToTileFolder, String pathToMapsFolder) {
        getTileImages(pathToTileFolder);
        loadMaps(pathToMapsFolder);
    }

    private void getTileImages(String pathToTileFolder) {
        try {
            String imgSrcStrings[] = getMetadata(pathToTileFolder);

            BufferedImage imgsMatrix[][] = new BufferedImage[imgSrcStrings.length][];
            for (int i = 0; i < imgSrcStrings.length; i++) {
                BufferedImage src = ImageIO.read(getClass().getResourceAsStream(pathToTileFolder + imgSrcStrings[i]));

                imgsMatrix[i] = splitSourceImage(src, imgSrcStrings[i]);
            }

            int tileIndex = 1;
            for (int i = 0; i < imgsMatrix.length; i++) {
                tileIndex = convertImgsToTile(imgsMatrix[i], tileIndex);
            }
        } catch (IOException e) {
            System.out.println("Failed to load background tiles:");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public int convertImgsToTile(BufferedImage[] imgs, int tileIndex) {
        Tile tile = new Tile();
        tile.addImgs(imgs);
        tiles.put(String.valueOf(tileIndex), tile);

        return tileIndex + 1;
    }

    private BufferedImage[] splitSourceImage(BufferedImage src, String imgSrcString) {
        String metadata[] = imgSrcString.split("_");
        metadata[3] = metadata[3].substring(0, 2); // removing .png

        int rows = Integer.parseInt(metadata[1]);
        int columns = Integer.parseInt(metadata[2]);
        int nImages = Integer.parseInt(metadata[3]);

        BufferedImage[] splitImgs = ImageUtils.getAllSubImages(src, rows, columns, nImages);
        for (int i = 0; i < splitImgs.length; i++) {
            splitImgs[i] = ImageUtils.resizeImage(splitImgs[i], gp.tileSize, gp.tileSize);
        }

        return splitImgs;

    }

    public void loadMaps(String pathToMapsFolder) {
        try {
            String mapPaths[] = getMetadata(pathToMapsFolder);

            for (int i = 0; i < mapPaths.length; i++) {
                loadMap(pathToMapsFolder + mapPaths[i], i);
            }

        } catch (IOException e) {
            System.out.println("Failed to load map:");
            e.printStackTrace();
            System.exit(0);
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
                    // + layer != 0 ? 1 : 0 *
                    // int tileType = Integer.parseInt(numbers[col]);
                    mapLayeredTilesTypes[layer][col][row] = numbers[col];
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

    private void createCurMap() {
        int col = 0;
        int row = 0;
        while (col < gp.maxScreenCol && row < gp.maxScreenRow) {

            for (int i = 0; i < mapLayeredTilesTypes.length; i++) {

            }
        }
    }

    protected String[] getMetadata(String pathToFolder) throws IOException {
        InputStream is = getClass().getResourceAsStream(pathToFolder + "metadata.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        int linesToRead = Integer.parseInt(br.readLine());
        String metadata[] = new String[linesToRead];

        String line;
        for (int i = 0; i < linesToRead; i++) {
            line = br.readLine();
            metadata[i] = line.strip();
        }
        br.close();

        return metadata;
    }

    public void draw(Graphics2D g2) {
        for (int i = 0; i < nLayers; i++) {
            drawLayer(g2, mapLayeredTilesTypes[i]);
        }
    }

    private void drawLayer(Graphics2D g2, String[][] mapTilesTypes) {
        int col = 0;
        int row = 0;

        int worldX = gp.camera.coorX;
        int worldY = gp.camera.coorY;
        int offsetX = -worldX;
        int offsetY = -worldY;
        int x = offsetX;
        int y = offsetY;

        String mapTileMD;
        Matcher matchedMD;
        Tile tile;

        while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
            mapTileMD = mapTilesTypes[col][row];

            if (!mapTileMD.equals("0")) {
                matchedMD = tileUtil.readMapMD(mapTileMD);
                tile = tiles.get(matchedMD.group("key"));
                tile.draw(g2, x, y, Integer.parseInt(matchedMD.group("var")));
            }
            col++;

            x += gp.tileSize;
            if (col == gp.maxScreenCol) {
                col = 0;
                x = offsetX;
                row++;
                y += gp.tileSize;
            }
        }

    }
}
