package Main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import Entities.Player;
import Tile.BackgroundManager;

public class GamePanel extends JPanel implements Runnable {
    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3; // To scale it up for visibility

    public final int tileSize = originalTileSize * scale; // 48x48 tile

    public final int maxScreenCol = 32;
    public final int maxScreenRow = 18;

    final int screenWidth = tileSize * maxScreenCol;
    final int halfScreenWidth = screenWidth / 2;
    final int screenHeight = tileSize * maxScreenRow;
    final int halfScreenHeight = screenHeight / 2;

    final public int worldWidth = tileSize * maxScreenCol;
    final public int worldHeight = tileSize * maxScreenRow;
    // these need to update when panel size updates
    public int screenCoorX, screenCoorY; // the coordinates of the top left corner given in world coordinates

    final int FPS = 24;

    KeyHandler keyH = new KeyHandler();

    Thread gameThread;
    Player player = new Player(this, keyH);
    BackgroundManager bgM = new BackgroundManager(this, 2);
    public CollisionChecker collisionChecker = new CollisionChecker(this);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth / 2, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);

        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    private void setScreenCoor() {
        screenCoorX = (int) (player.screenX - 0.5 * this.getWidth()); // -1 because 0 indexing;
        screenCoorY = (int) (player.screenY - 0.5 * this.getHeight());
    }

    public void updateScreenCoor(int deltaX, int deltaY) {
        if (player.worldX >= 0.5 * this.getWidth() && (player.worldX + this.getWidth()) <= worldWidth) {
            screenCoorX += deltaX;
        }
        if (player.worldY >= 0.5 * this.getHeight() && (player.worldY + this.getHeight()) <= worldWidth) {
            screenCoorY += deltaY;
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();

        // Needs to be set after panel is started
        setScreenCoor();

    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;

        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;

            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }

        }
    }

    public void update() {
        player.update();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        bgM.draw(g2);
        player.draw(g2);

        if (keyH.showDebugTool) {
            drawDebugTool(g2);
        }

        g2.dispose();
    }

    public void drawDebugTool(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.drawString("(" + player.worldX + ", " + player.worldY + ")", this.getWidth() - 150, 50);
    }
}
