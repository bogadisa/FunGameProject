package Main;

// import javax.swing.JFrame;

import secondEngine.Window;
import secondEngine.renderer.Shader;

public class Main {
    // public static void main(String[] args) {

    // JFrame window = new JFrame();
    // window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // window.setResizable(false);
    // window.setTitle("My First Game");

    // GamePanel gamePanel = new GamePanel();
    // window.add(gamePanel);

    // window.pack();

    // window.setLocationRelativeTo(null);
    // window.setVisible(true);

    // gamePanel.startGameThread();
    // }
    public static void main(String[] args) {
        Window window = Window.get();
        window.run();
        // Shader shader = new Shader("../../shaders/sprites/default.glsl");
    }
}