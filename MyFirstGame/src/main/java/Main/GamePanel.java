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
    final int originalTileSize = 48; // 16x16 tile
    final int scale = 1; // To scale it up for visibility

    public final int tileSize = originalTileSize * scale; // 48x48 tile

    public final int maxScreenCol = 16;
    public final int maxScreenRow = 9;

    final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    final int centerWorldCoorX = screenWidth / 2 - 1;
    final int centerWorldCoorY = screenHeight / 2 - 1; // -1 because 0 indexing
    // final int centerWorldCoorX = 0; // testing
    // final int centerWorldCoorY = 0; // testing
    // these need to update when panel size updates
    public int screenCoorX, screenCoorY; // the coordinates of the top left corner given in world coordinates

    final int FPS = 24;

    KeyHandler keyH = new KeyHandler();

    Thread gameThread;
    Player player = new Player(this, keyH);
    BackgroundManager bgM = new BackgroundManager(this, 2);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);

        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    private void setScreenCoor() {
        Dimension dim = this.getSize();
        int centerScreenX = (int) (dim.getWidth() / 2);
        int centerScreenY = (int) (dim.getHeight() / 2);

        screenCoorX = centerWorldCoorX - centerScreenX;
        screenCoorY = centerWorldCoorY - centerScreenY;
    }

    public void updateScreenCoor(int deltaX, int deltaY) {
        screenCoorX += deltaX;
        screenCoorY += deltaY;
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

        g2.dispose();
    }
}
