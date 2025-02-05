package Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed, pressed;
    public boolean spacePressed;
    private boolean ctrlPressed;

    
    public boolean showDebugTool = false;
    public boolean playerGodMode = false;
    public boolean playerCollision = true;

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (ctrlPressed) {
            if (code == KeyEvent.VK_P) {
                showDebugTool = !showDebugTool;
            }
            if (code == KeyEvent.VK_G) {
                playerGodMode = !playerGodMode;
                if (!playerGodMode) {
                    playerCollision = true;
                }
            }
            if (code == KeyEvent.VK_H && playerGodMode) {
                playerCollision = !playerCollision;
            }
        } else {
            if (code == KeyEvent.VK_CONTROL) {
                ctrlPressed = true;
            }
            if (code == KeyEvent.VK_W) {
                upPressed = true;
                pressed = true;
            }
            if (code == KeyEvent.VK_S) {
                downPressed = true;
                pressed = true;
            }
            if (code == KeyEvent.VK_A) {
                leftPressed = true;
                pressed = true;
            }
            if (code == KeyEvent.VK_D) {
                rightPressed = true;
                pressed = true;
            }
            if (code == KeyEvent.VK_SPACE) {
                spacePressed = true;
                pressed = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_CONTROL) {
            ctrlPressed = false;
        }

        if (code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }
        if (code == KeyEvent.VK_SPACE) {
            spacePressed = false;
        }

        if (!upPressed && !downPressed && !leftPressed && !rightPressed && !spacePressed) {
            pressed = false;
        }
    }

}
