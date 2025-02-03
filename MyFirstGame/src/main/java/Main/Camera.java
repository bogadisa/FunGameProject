package Main;

import Entities.Player;

public class Camera {
    public int coorX, coorY;

    GamePanel gp;
    Player player;
    public Camera(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;
    }

    public void setCameraCoor() {
        coorX = (int) (player.screenX - 0.5 * gp.getWidth());
        coorY = (int) (player.screenY - 0.5 * gp.getHeight());
    }

    public void updateScreenCoor(int deltaX, int deltaY) {
        int width = gp.getWidth();
        int height = gp.getHeight();
        if (player.worldX >= 0.5 * width && (player.worldX + width) <= gp.worldWidth) {
            if (deltaX < 0) {
                if (coorX < -deltaX){
                    coorX = 0;
                } else {
                    coorX += deltaX;
                }
            } else {
                if (gp.worldWidth - coorX + width < deltaX){
                    coorX = gp.worldWidth - width;
                } else {
                    coorX += deltaX;
                }
            }
        }
        if (player.worldY >= 0.5 * height && (player.worldY + height) <= gp.worldHeight) {
            if (deltaY < 0) {
                if (coorY < -deltaY){
                    coorY = 0;
                } else {
                    coorY += deltaY;
                }
            } else {
                if (gp.worldHeight - coorY + height < deltaY){
                    coorY = gp.worldHeight - height;
                } else {
                    coorY += deltaY;
                }
            }
        }
    }
}
