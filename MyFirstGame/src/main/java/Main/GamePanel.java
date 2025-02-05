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

    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    final public int worldWidth = tileSize * maxScreenCol;
    final public int worldHeight = tileSize * maxScreenRow;
    // these need to update when panel size updates

    final int targetFPS = 24;
    double actualFPS;

    KeyHandler keyH = new KeyHandler();

    Thread gameThread;
    Player player = new Player(this, keyH);
    public Camera camera = new Camera(this, player);
    public BackgroundManager bgM = new BackgroundManager(this, 2);
    public CollisionChecker collisionChecker = new CollisionChecker(this);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);

        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();

        // Needs to be set after panel is started
        camera.setCameraCoor();

    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / targetFPS;
        double delta = 0;

        long lastTime = System.nanoTime();
        long lastDrawTime = System.nanoTime();
        long currentTime;
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;

            lastTime = currentTime;

            if (delta >= 1) {
                actualFPS = targetFPS * (currentTime - lastDrawTime) / drawInterval;
                update();
                repaint();
                lastDrawTime = System.nanoTime();
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
        if (keyH.playerGodMode) {
            player.drawGodModeOverview(g2);
        }

        g2.dispose();
    }

    public void drawDebugTool(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.drawString("FPS: " + actualFPS, 5, 15);
        g2.drawString("Camera: (" + camera.coorX + ", " + camera.coorY + ")", 50, 50);
        g2.drawString("Player: " + player.getPositionAsString(), 50, 75);
        if (!keyH.playerGodMode) {
            g2.drawString("Player god mode enabled: " + keyH.playerGodMode, 50, 100);
        }
    }
}
