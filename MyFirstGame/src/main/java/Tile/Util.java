package Tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    private Pattern pattern = Pattern.compile("(?<A>C|S|P)(?<B>N|E|W|S)(?<C>S|A)(?<D>O|[0-9])(?<X>[0-9]{2})-(?<Y>[0-9]*)-(?<Z>[0-9]*)");

    protected HashMap<String, Tile> tiles;

    public Util(HashMap<String, Tile> tiles) {
        this.tiles = tiles;
    }

    

    /**
     * Reads the metadata of the tile, provided by the map
     * The metadata follows the pattern:
     *      ABCDX-Y-Z
     * Please note:
     *      These may not all be implemented
     * 
     * Where:
     *  A = {C, S, P} 
     *      signaling if tile has collision enabled (C), or not (P), or only slows you down (S)
     *      If (C) or (S) ->  Overrides the default value
     *  B = {N, E, W, S}
     *      The direction the tile should face (north, east, west, south)
	 *		Should directly correlate with tile variation Z
	 *		Its purpose is to easily communicate with neighboring tiles to determine their variation
	 *		Connecting textures essentially
     *  C = {S, A} 
     *      Static or animated
     *  D = {O, [0-9]}
     *      How opaque the tile is
     *      Where O is fully opaque
     *      and 0-9 represents the level of opaquenes, 0 meaning invisible
     *  X: of format XX 
     *      showing which layer it belongs to
	 *		    Might be unnecessary
	 *	B: of format X
      *     tile type
     *  Z: of format X
     *      tile variation

     * @param metadata
     */
    Matcher readTileMD(String metadata) {
        return pattern.matcher(metadata);
    }

    /**
     * Only for intial map data, converts to current map data format
     * @param tileKey ABCDX-Y-Z format
     * @return A-Y-Z formatted key
     */
    public String getFormattedKey(String tileKey) {
        Matcher matcher = readTileMD(tileKey);

        return matcher.group("A") + "-" +  matcher.group("Y") + "-" + matcher.group("Z");
    }

    /**
     * Reads the meta data of both tiles and decides how to combine them
     * 
     * @param tileImg1 The image with higher priority
     * @param metadata1
     * @param tileImg2 The image with lower priority
     * @param metadata2
     */
    public String compareTiles(BufferedImage tileImg1, String metadata1, BufferedImage tileImg2) {
        Matcher matcher = readTileMD(metadata1);

        if (matcher.group("D").equals("O")) {
            return "";
        }

        BufferedImage newImg = new BufferedImage(tileImg1.getWidth(), tileImg1.getHeight(), tileImg1.getType());
        Graphics2D g2 = newImg.createGraphics();
        g2.drawImage(tileImg1, 0, 0, null);
        g2.drawImage(tileImg2, 0, 0, null);

        BufferedImage imgs[] = {newImg};

        Tile tile = new Tile();
        tile.images = imgs;

        String tileKey = String.valueOf(tiles.size());

        tiles.put(tileKey, tile);

        return tileKey;
    }
}
