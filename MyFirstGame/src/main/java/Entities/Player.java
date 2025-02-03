package Entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Main.GamePanel;
import Main.KeyHandler;

public class Player extends Entity {
    KeyHandler keyH;

    public Player(GamePanel gp, KeyHandler keyH) {
        super(gp);
        this.keyH = keyH;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = 100;
        worldY = 100;
        updateScreenCoor();

        offsetSolidAreaX = 10;
        offsetSolidAreaY = 2;
        solidArea = new Rectangle(offsetSolidAreaX, offsetSolidAreaY, 28, 46);

        defaultSpeed = 10;

        direction = "down";
    }

    public void getPlayerImage() {
        try {
            imgSrcString = "./player_3_3_8.png";

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

        speed = defaultSpeed;
        if (right != 0 && down != 0) {
            speed = (int) Math.sqrt(defaultSpeed);
        }

        speedX = right * speed;
        speedY = down * speed;

        gp.collisionChecker.checkCollision(this);

        worldX += speedX;
        worldY += speedY;
        // if we want to follow the player
        gp.updateScreenCoor((int) (speedX), (int) (speedY));

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
        // TODO is this smart?
        updateScreenCoor();

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

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }
}
