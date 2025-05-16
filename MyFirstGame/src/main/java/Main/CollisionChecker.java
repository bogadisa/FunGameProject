package Main;

import java.awt.Rectangle;
import java.util.regex.Matcher;

import Entities.Entity;
import Tile.Tile;
import Tile.TileUtil;

public class CollisionChecker {
    GamePanel gp;
    TileUtil tileUtil;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
        tileUtil = new TileUtil(gp.bgM.tiles);
    }

    private boolean checkTile(int col, int row) {
        // stops entity from going out of bounds
        if (col < 0 || row < 0) {
            return true;
        } else if (col >= gp.bgM.mapLayeredTilesTypes[0].length || row >= gp.bgM.mapLayeredTilesTypes[0][0].length) {
            return true;
        }
        Tile tile;
        String mapTileMD;
        Matcher matchedMD;
        for (int i = 0; i < gp.bgM.nLayers; i++) {
            mapTileMD = gp.bgM.mapLayeredTilesTypes[i][col][row];
            if (!mapTileMD.equals("0")) {
                matchedMD = tileUtil.readMapMD(mapTileMD);
                tile = gp.bgM.tiles.get(matchedMD.group("key"));
                if (tile.colision) {
                    return true;
                }
            }

        }
        return false;
    }

    public int getCollisionSafeSpeedX(Entity entity, int x, int y, int topCornerCol, int topCornerRow, int botCornerCol,
            int botCornerRow) {
        Rectangle solidArea = entity.getSolidRectangle();
        int entityWidth = solidArea.width;
        boolean collision = false;
        if (entity.speedX < 0) {
            int distToNextCol = (topCornerCol) * gp.tileSize - x;
            if (entity.speedX <= distToNextCol) {
                collision = checkTile(topCornerCol - 1, topCornerRow) || checkTile(topCornerCol - 1, botCornerRow);
                if (collision) {
                    // stops entity from entering the next tile
                    return distToNextCol;
                }
            }

        } else if (entity.speedX > 0) {
            int distToNextCol = (botCornerCol + 1) * gp.tileSize - (x + entityWidth);

            if (entity.speedX >= distToNextCol) {
                collision = checkTile(botCornerCol + 1, botCornerRow) || checkTile(botCornerCol + 1, topCornerRow);
                if (collision) {
                    return distToNextCol - 1;
                }
            }
        }
        return entity.speedX;
    }

    public int getCollisionSafeSpeedX(Entity entity) {
        Rectangle solidArea = entity.getSolidRectangle();
        int x = solidArea.x;
        int entityWidth = solidArea.width;
        int y = solidArea.y;
        int entityHeight = solidArea.height;

        int topCornerCol = (int) (x / gp.tileSize);
        int topCornerRow = (int) (y / gp.tileSize);
        int botCornerCol = (int) ((x + entityWidth) / gp.tileSize);
        int botCornerRow = (int) ((y + entityHeight) / gp.tileSize);

        return getCollisionSafeSpeedX(entity, x, y, topCornerCol, topCornerRow, botCornerCol, botCornerRow);
    }

    public int getCollisionSafeSpeedY(Entity entity, int speedY, int x, int y, int topCornerCol, int topCornerRow,
            int botCornerCol,
            int botCornerRow) {
        Rectangle solidArea = entity.getSolidRectangle();
        int entityHeight = solidArea.height;
        boolean collision = false;
        if (speedY < 0) {
            int distToNextRow = (topCornerRow) * gp.tileSize - y;
            if (speedY <= distToNextRow) {
                collision = checkTile(topCornerCol, topCornerRow - 1) || checkTile(botCornerCol, topCornerRow - 1);
                if (collision) {
                    return  distToNextRow + 1;
                }
            }

        } else if (speedY > 0) {
            int distToNextRow = (botCornerRow + 1) * gp.tileSize - (y + entityHeight);
            if (speedY >= distToNextRow) {
                collision = checkTile(botCornerCol, botCornerRow + 1) || checkTile(topCornerCol, botCornerRow + 1);
                if (collision) {
                    return  distToNextRow - 1;
                }
            }
        }
        return speedY;
    }

    public int getCollisionSafeSpeedY(Entity entity, int speedY) {
        Rectangle solidArea = entity.getSolidRectangle();
        int x = solidArea.x;
        int entityWidth = solidArea.width;
        int y = solidArea.y;
        int entityHeight = solidArea.height;

        int topCornerCol = (int) (x / gp.tileSize);
        int topCornerRow = (int) (y / gp.tileSize);
        int botCornerCol = (int) ((x + entityWidth) / gp.tileSize);
        int botCornerRow = (int) ((y + entityHeight) / gp.tileSize);

        return getCollisionSafeSpeedY(entity, speedY, x, y, topCornerCol, topCornerRow, botCornerCol, botCornerRow);

    }

    public int getCollisionSafeSpeedY(Entity entity) {
        int entitySpeedY = entity.speedY;

        return getCollisionSafeSpeedY(entity, entitySpeedY);
    }

    /**
     * Checks if the entity would collide, given their current speeds.
     * @param enity The entity to check if its gonna colide
     * @return A pair of speeds which are guarnteed not to collide.
     */
    public int[] getCollisionSafeSpeeds(Entity entity) {
        Rectangle solidArea = entity.getSolidRectangle();
        int x = solidArea.x;
        int entityWidth = solidArea.width;
        int y = solidArea.y;
        int entityHeight = solidArea.height;

        int entitySpeedY = entity.speedY;

        int topCornerCol = (int) (x / gp.tileSize);
        int topCornerRow = (int) (y / gp.tileSize);
        int botCornerCol = (int) ((x + entityWidth) / gp.tileSize);
        int botCornerRow = (int) ((y + entityHeight) / gp.tileSize);

        int speedX = getCollisionSafeSpeedX(entity, x, y, topCornerCol, topCornerRow, botCornerCol, botCornerRow);
        int speedY = getCollisionSafeSpeedY(entity, entitySpeedY, x, y, topCornerCol, topCornerRow, botCornerCol,
                botCornerRow);

        return new int[]{speedX, speedY};
    }
}
