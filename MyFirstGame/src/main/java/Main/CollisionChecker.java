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

    public boolean checkRowCollision(Entity entity, int x, int y, int topCornerCol, int topCornerRow, int botCornerCol,
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
                    entity.speedX = distToNextCol + 1;
                }
            }

        } else if (entity.speedX > 0) {
            int distToNextCol = (botCornerCol + 1) * gp.tileSize - (x + entityWidth);

            if (entity.speedX >= distToNextCol) {
                collision = checkTile(botCornerCol + 1, botCornerRow) || checkTile(botCornerCol + 1, topCornerRow);
                if (collision) {
                    entity.speedX = distToNextCol - 1;
                }
            }
        }
        return collision;
    }

    public boolean checkRowCollision(Entity entity) {
        Rectangle solidArea = entity.getSolidRectangle();
        int x = solidArea.x;
        int entityWidth = solidArea.width;
        int y = solidArea.y;
        int entityHeight = solidArea.height;

        int topCornerCol = (int) (x / gp.tileSize);
        int topCornerRow = (int) (y / gp.tileSize);
        int botCornerCol = (int) ((x + entityWidth) / gp.tileSize);
        int botCornerRow = (int) ((y + entityHeight) / gp.tileSize);

        return checkRowCollision(entity, x, y, topCornerCol, topCornerRow, botCornerCol, botCornerRow);
    }

    public boolean checkColCollision(Entity entity, int x, int y, int topCornerCol, int topCornerRow, int botCornerCol,
            int botCornerRow) {
        Rectangle solidArea = entity.getSolidRectangle();
        int entityHeight = solidArea.height;
        boolean collision = false;
        if (entity.speedY < 0) {
            int distToNextRow = (topCornerRow) * gp.tileSize - y;
            if (entity.speedY <= distToNextRow) {
                collision = checkTile(topCornerCol, topCornerRow - 1) || checkTile(botCornerCol, topCornerRow - 1);
                if (collision) {
                    entity.speedY = distToNextRow + 1;
                }
            }

        } else if (entity.speedY > 0) {
            int distToNextRow = (botCornerRow + 1) * gp.tileSize - (y + entityHeight);
            if (entity.speedY >= distToNextRow) {
                collision = checkTile(botCornerCol, botCornerRow + 1) || checkTile(topCornerCol, botCornerRow + 1);
                if (collision) {
                    entity.speedY = distToNextRow - 1;
                }
            }
        }
        return collision;
    }

    public boolean checkColCollision(Entity entity) {
        Rectangle solidArea = entity.getSolidRectangle();
        int x = solidArea.x;
        int entityWidth = solidArea.width;
        int y = solidArea.y;
        int entityHeight = solidArea.height;

        int topCornerCol = (int) (x / gp.tileSize);
        int topCornerRow = (int) (y / gp.tileSize);
        int botCornerCol = (int) ((x + entityWidth) / gp.tileSize);
        int botCornerRow = (int) ((y + entityHeight) / gp.tileSize);

        return checkColCollision(entity, x, y, topCornerCol, topCornerRow, botCornerCol, botCornerRow);
    }

    public void checkCollision(Entity entity) {
        Rectangle solidArea = entity.getSolidRectangle();
        int x = solidArea.x;
        int entityWidth = solidArea.width;
        int y = solidArea.y;
        int entityHeight = solidArea.height;

        int topCornerCol = (int) (x / gp.tileSize);
        int topCornerRow = (int) (y / gp.tileSize);
        int botCornerCol = (int) ((x + entityWidth) / gp.tileSize);
        int botCornerRow = (int) ((y + entityHeight) / gp.tileSize);

        checkRowCollision(entity, x, y, topCornerCol, topCornerRow, botCornerCol, botCornerRow);
        checkColCollision(entity, x, y, topCornerCol, topCornerRow, botCornerCol, botCornerRow);
    }
}
