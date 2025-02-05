package Main;

import java.awt.Rectangle;

import Entities.Player;

public class Camera {
    public int coorX, coorY;
    int defaultYOffset;

    GamePanel gp;
    Player player;
    public Camera(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;

        this.defaultYOffset = player.defaultYOffset;
    }

    public void setCameraCoor() {
        coorX = (int) (player.screenX - 0.5 * gp.getWidth());
        coorY = (int) (player.screenY - 0.5 * gp.getHeight());
    }

    public void updateScreenCoor(int deltaX, int deltaY) {
        int screenWidth = gp.getWidth();
        int screenHeight = gp.getHeight();
        Rectangle solidArea = player.getSolidRectangle();
        int playerX = solidArea.x;
        int playerY = solidArea.y;
        int playerWidth = solidArea.width;
        int playerHeight = solidArea.height;
        if (playerX >= 0.5 * screenWidth && (playerX + playerWidth + 0.5*screenWidth) <= gp.worldWidth) {
            if (deltaX < 0) {
                if (coorX < -deltaX){
                    coorX = 0;
                } else {
                    coorX += deltaX;
                }
            } else {
                if (gp.worldWidth - coorX + screenWidth < deltaX){
                    coorX = gp.worldWidth - screenWidth;
                } else {
                    coorX += deltaX;
                }
            }
        }
        if ((playerY) >= 0.5 * (screenHeight) && (playerY + playerHeight + 0.5*screenHeight + 191) <= gp.worldHeight) {
            if (deltaY < 0) {
                if (coorY < -deltaY){
                    coorY = 0;
                } else {
                    coorY += deltaY;
                }
            } else {
                if (gp.worldHeight - coorY + screenHeight < deltaY){
                    coorY = gp.worldHeight - screenHeight;
                } else {
                    coorY += deltaY;
                }
            }
        }
    }
}
