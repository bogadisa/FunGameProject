package Main;

public class Time {
    int targetFPS;
    public double actualFPS;

    long lastTime;
    long lastDrawTime;
    long currentTime;
    double drawInterval;

    public double delta = 0;

    public Time(int targetFPS) {
        this.targetFPS = targetFPS;
        this.drawInterval = 1000000000 / targetFPS;
    }

    public void startLoop() {
        lastTime = System.nanoTime();
        lastDrawTime = System.nanoTime();
    }

    public void increment() {
        currentTime = System.nanoTime();
        delta += (currentTime - lastTime) / drawInterval;
        lastTime = currentTime;

    }

    public void update() {
        actualFPS = targetFPS * (currentTime - lastDrawTime) / drawInterval;
        lastDrawTime = System.nanoTime();
        delta--;
    }

    public boolean readyToDraw() {
        return delta >= 1;
    }
    
}
