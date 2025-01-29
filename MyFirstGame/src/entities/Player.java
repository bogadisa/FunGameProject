package Entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Main.GamePanel;
import Main.KeyHandler;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        x = 100;
        y = 100;
        speed = 4;

        direction = "down";
    }

    public void getPlayerImage() {
        try {
            imgSrcString = "./entities/player_3_3_8.png";
            // File f = new File(imgSrcString);
            // src = ImageIO.read(f);
            // System.out.println(f.exists());
            src = ImageIO.read(getClass().getResourceAsStream(imgSrcString));
            splitSourceImage();
        } catch (IOException e) {
            System.out.println("Failed to load player sprites:");
            e.printStackTrace();
        }
    }

    public void update() {

        if (!keyH.pressed) {
            return;
        }

        int right = 0;
        int down = 0;

        if (keyH.upPressed) {
            down = -1;
            direction = "up";
        } else if (keyH.downPressed) {
            down = 1;
            direction = "down";
        }
        if (keyH.rightPressed) {
            right = 1;
            direction = "right";
        } else if (keyH.leftPressed) {
            right = -1;
            direction = "left";
        }

        double currentSpeed = speed;
        if (right != 0 && down != 0) {
            currentSpeed = Math.sqrt(speed);
        }

        x += right * currentSpeed;
        y += down * currentSpeed;

        spriteCounter++;
        if (spriteCounter >= 6) {
            spriteNumber++;
            if (spriteNumber == up.length) {
                spriteNumber = 0;
            }
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2) {

        BufferedImage image = null;

        switch (direction) {
            case "up":
                image = up[spriteNumber];
                break;

            case "down":
                image = down[spriteNumber];
                break;

            case "left":
                image = left[spriteNumber];
                break;

            case "right":
                image = right[spriteNumber];
                break;

            default:
                break;
        }

        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
    }
}
