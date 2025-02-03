package Tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tile {

    public BufferedImage[] images;
    public boolean colision = false;

    Pattern localPattern = Pattern.compile("(?<key>(?<A>C|S|P)-(?<Y>[0-9]+))-(?<var>[0-9]+)");

    // public void readTileMD(String metadata) {
    // Matcher matcher = localPattern.matcher(metadata);
    // matcher.group("Z");

    // }

    public BufferedImage getImageFromTileMD(String metadata) {
        Matcher matcher = localPattern.matcher(metadata);
        int idx = Integer.valueOf(matcher.group("Z"));

        return images[idx];

    }

    public void addImgs(BufferedImage[] imgs) {
        images = imgs;

    }

    public void addImgs(BufferedImage[] imgs, int from, int to) {
        images = Arrays.copyOfRange(imgs, from, to);
    }

    public void draw(Graphics2D g2, int x, int y) {
        g2.drawImage(images[0], x, y, null);
    }

    public void draw(Graphics2D g2, int x, int y, int variation) {
        g2.drawImage(images[variation], x, y, null);
    }

    public void draw(Graphics2D g2, int x, int y, String metadata) {
        g2.drawImage(getImageFromTileMD(metadata), x, y, null);
    }
}
