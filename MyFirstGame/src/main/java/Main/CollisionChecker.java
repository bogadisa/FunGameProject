package Main;

import java.awt.Rectangle;

import Entities.Entity;
import Tile.Tile;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    private boolean checkTile(int col, int row) {
        // stops entity from going out of bounds
        if (col < 0 || row < 0) {
            return true;
        } else if (col >= gp.bgM.mapLayeredTilesTypes[0].length || row >= gp.bgM.mapLayeredTilesTypes[0][0].length) {
            return true;
        }
        Tile tile;
        String tileType;
        for (int i = 0; i < gp.bgM.nLayers; i++) {
            tileType = gp.bgM.mapLayeredTilesTypes[i][col][row];
            if (!tileType.equals("0")){
                tile = gp.bgM.tiles.get(tileType);
                if (tile.colision) {
                    return true;
                }
            }
            
        }
        return false;
    }

    public void checkCollision(Entity entity) {
        Rectangle solidArea = entity.getSolidRectangle();
        int x = solidArea.x;
        int entityWidth = solidArea.width;
        int y = solidArea.y;
        int entityHeight = solidArea.height;

        int topCornerCol = (int)(x / gp.tileSize);
        int topCornerRow = (int)(y / gp.tileSize);
        int botCornerCol = (int)((x + entityWidth) / gp.tileSize);
        int botCornerRow = (int)((y + entityHeight) / gp.tileSize);

        boolean collisionX = false;
        if (entity.speedX < 0) {
            int distToNextCol = (topCornerCol) * gp.tileSize - x;
            if (entity.speedX <= distToNextCol) {
                collisionX = checkTile(topCornerCol - 1, topCornerRow) || checkTile(topCornerCol - 1, botCornerRow);
                if (collisionX) {
                    // stops entity from entering the next tile
                    entity.speedX = distToNextCol + 1;
                }
            }

        } 
        else if (entity.speedX > 0) {
            int distToNextCol = (botCornerCol + 1) * gp.tileSize - (x + entityWidth);
            
            if (entity.speedX >= distToNextCol) {
                collisionX = checkTile(botCornerCol + 1, botCornerRow) || checkTile(botCornerCol + 1, topCornerRow);
                if (collisionX) {
                    entity.speedX = distToNextCol - 1;
                }
            }
        }
        boolean collisionY;
        if (entity.speedY < 0) {
            int distToNextRow = (topCornerRow) * gp.tileSize - y;
            if (entity.speedY <= distToNextRow) {
                collisionY = checkTile(topCornerCol, topCornerRow - 1) || checkTile(botCornerCol, topCornerRow - 1);
                if (collisionY) {
                    entity.speedY = distToNextRow + 1;
                }
            }

        } 
        else if (entity.speedY > 0) {
            int distToNextRow = (botCornerRow + 1) * gp.tileSize - (y + entityHeight);
            if (entity.speedY >= distToNextRow) {
                collisionY = checkTile(botCornerCol, botCornerRow + 1) || checkTile(topCornerCol, botCornerRow + 1);
                if (collisionY) {
                    entity.speedY = distToNextRow - 1;
                }
            }
        }
    }
}
