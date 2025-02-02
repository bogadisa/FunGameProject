package Tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Tile {

    public BufferedImage[] images;
    public boolean colision = true;

    public void addImgs(BufferedImage[] imgs) {
        images = imgs;

    }

    public void draw(Graphics2D g2, int x, int y) {
        g2.drawImage(images[0], x, y, null);
    }
}
