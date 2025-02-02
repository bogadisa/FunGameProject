package Tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tile {

    public BufferedImage[] images;
    public boolean colision = true;

    Pattern localPattern = Pattern.compile("(?<A>C|S|P)-(?<Y>[0-9]*)-(?<Z>[0-9]*)");

    
    // public void readTileMD(String metadata) {
    //     Matcher matcher = localPattern.matcher(metadata);
    //     matcher.group("Z");

    // }

    public BufferedImage getImageFromTileMD(String metadata) {
        Matcher matcher = localPattern.matcher(metadata);
        int idx = Integer.valueOf(matcher.group("Z"));

        return images[idx];

    }

    public void addImgs(BufferedImage[] imgs) {
        images = imgs;

    }

    public void draw(Graphics2D g2, int x, int y) {
        g2.drawImage(images[0], x, y, null);
    }

    public void draw(Graphics2D g2, int x, int y, String metadata) {
        g2.drawImage(getImageFromTileMD(metadata), x, y, null);
    }
}
