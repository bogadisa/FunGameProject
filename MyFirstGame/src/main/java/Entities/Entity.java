package Entities;

import java.awt.image.BufferedImage;

import Main.GamePanel;
import Utils.ImageUtils;

public class Entity {
    protected GamePanel gp;

    public int worldX, worldY;
    public int screenX, screenY;
    public int speed;

    public String imgSrcString;
    public BufferedImage src;
    public BufferedImage[] up = new BufferedImage[2], down = new BufferedImage[2], left = new BufferedImage[2],
            right = new BufferedImage[2];

    public String direction;
    public int spriteCounter = 0;
    public int spriteNumber = 0;

    protected Entity(GamePanel gp) {
        this.gp = gp;
    }

    public void updateScreenCoor() {
        screenX = worldX - gp.screenCoorX;
        screenY = worldY - gp.screenCoorY;
    }

    public void splitSourceImage() {
        String metadata[] = imgSrcString.split("_");
        metadata[3] = metadata[3].substring(0, 1); // removing .png

        int rows = Integer.parseInt(metadata[1]);
        int columns = Integer.parseInt(metadata[2]);
        int nImages = Integer.parseInt(metadata[3]);

        BufferedImage imgs[] = ImageUtils.getAllSubImages(src, rows, columns, nImages);

        down[0] = imgs[0];
        down[1] = imgs[1];

        up[0] = imgs[2];
        up[1] = imgs[3];

        left[0] = imgs[4];
        left[1] = imgs[5];

        right[0] = imgs[6];
        right[1] = imgs[7];
    }
}
