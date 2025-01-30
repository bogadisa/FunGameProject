package Tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Tile {

    public BufferedImage[] images;
    public boolean colision = false;
    public boolean checkSur = false; // set this automatically based on length of images

    public void addImgs(BufferedImage[] imgs) {
        images = imgs;
        if (images.length > 1) {
            checkSur = true;
        }

    }

    // private void checkPattern(String pattern) {
    // int patternNum = Integer.parseInt(pattern, 2);

    // int idx;
    // if (patternNum % 3 == 0) {
    // idx = patternNum / 3;
    // } else if (patternNum )
    // }

    public void draw(Graphics2D g2, int x, int y, int tileSize) {
        g2.drawImage(images[0], x, y, tileSize, tileSize, null);
    }
}
