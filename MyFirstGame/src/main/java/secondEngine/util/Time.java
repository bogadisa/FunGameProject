package secondEngine.util;

public class Time {
    private static Time instance = null;
    
    private int targetFPS;
    private double actualFPS;
    private long lastTime;
    private long lastDrawTime;
    private long currentTime;
    private double drawInterval;
    private double delta;

    private Time() {
        this.targetFPS = 24;
        this.drawInterval = 1000000000 / targetFPS;
        this.delta = 0;
    }

    public static Time get() {
        if (Time.instance == null) {
            Time.instance = new Time();
        }

        return Time.instance;
    }

    public static void startLoop() {
        get().lastTime = System.nanoTime();
        get().lastDrawTime = System.nanoTime();
    }

    public static void increment() {
        get().currentTime = System.nanoTime();
        get().delta += (get().currentTime - get().lastTime) / get().drawInterval;
        get().lastTime = get().currentTime;

    }

    public static void update() {
        get().actualFPS = get().targetFPS * (get().currentTime - get().lastDrawTime) / get().drawInterval;
        get().lastDrawTime = System.nanoTime();
        get().delta--;
    }

    public static boolean readyToDraw() {
        return get().delta >= 1;
    }

    public static double getDelta() {
        return get().delta;
    }

    public static double getFPS() {
        return get().actualFPS;
    }
}
