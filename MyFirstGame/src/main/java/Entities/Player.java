package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Main.GamePanel;
import Main.KeyHandler;

public class Player extends Entity {
    KeyHandler keyH;
    public int defaultYOffset = 191;

    public boolean godMode = false;
    public boolean collisionEnabled = true;

    public Player(GamePanel gp, KeyHandler keyH) {
        super(gp);
        this.keyH = keyH;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = (int) (0.5 * gp.worldWidth);
        worldY = (int) (0.5 * gp.worldHeight) + defaultYOffset;

        screenX = (int) (0.5 * gp.screenWidth);
        screenY = (int) (0.5 * gp.screenHeight);

        offsetSolidAreaX = 10;
        offsetSolidAreaY = 2;
        solidArea = new Rectangle(offsetSolidAreaX, offsetSolidAreaY, 28, 46);

        defaultSpeed = 10;

        direction = "down";
    }

    public void drawGodModeOverview(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.drawString("Player god mode enabled: " + keyH.playerGodMode, 50, 100);
        g2.drawString("Collision enabled: " + keyH.playerCollision, 60, 125);

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
        godMode = keyH.playerGodMode;
        collisionEnabled = keyH.playerCollision;

        if (!godMode) {
            calcGravity();
        }

        if (keyH.pressed) {
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
            if (keyH.spacePressed && onGround) {
                downwardMomentum -= 20;
            }

            speed = defaultSpeed;
            if (right != 0 && down != 0) {
                speed = (int) Math.sqrt(defaultSpeed);
            }

            speedX = right * speed;
            speedY = down * speed;

            spriteCounter++;
            if (spriteCounter >= 3) {
                spriteNumber++;
                if (spriteNumber == up.length) {
                    spriteNumber = 0;
                }
                spriteCounter = 0;
            }
        } else {
            speedX = 0;
            speedY = 0;
        }

        resolveUpwardMomentum();
        if (collisionEnabled) {
            int updatedSpeed[] = gp.collisionChecker.getCollisionSafeSpeeds(this);
            speedX = updatedSpeed[0];
            speedY = updatedSpeed[1];
        }
        gp.camera.updateScreenCoor((int) (speedX), (int) (speedY));

        worldX += speedX;
        worldY += speedY;
        // if we want to follow the player

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
