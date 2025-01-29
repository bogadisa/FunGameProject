package Tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Tile {

    public BufferedImage image;
    public boolean colision = false;

    public void draw(Graphics2D g2, int x, int y) {
        g2.drawImage(image, x, y, null);
    }
}
