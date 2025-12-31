package secondEngine.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Time {
    private interface CooldownCallback extends Callback<Boolean> {
        public void updateCooldown(double dt);
    }

    private static Time instance = null;

    private int targetFPS;
    private double actualFPS;
    private long lastTime;
    private long lastDrawTime;
    private long currentTime;
    private double drawInterval;
    private double delta;

    private Map<String, CooldownCallback> scheduledCooldowns;

    private Time() {
        this.targetFPS = 24;
        this.drawInterval = 1000000000 / targetFPS;
        this.delta = 0;
        this.scheduledCooldowns = new HashMap<>();
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
        get().delta = 0;
    }

    public static void increment() {
        get().currentTime = System.nanoTime();
        get().delta += (get().currentTime - get().lastTime) / get().drawInterval;
        get().lastTime = get().currentTime;

    }

    public static void update() {
        get().actualFPS = get().targetFPS * (get().currentTime - get().lastDrawTime) / get().drawInterval;
        get().lastDrawTime = System.nanoTime();
        updateScheduledCooldowns();
        // TODO should it just set it to zero?
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

    public static long getCurrentTime() {
        return get().currentTime;
    }

    public static double getDrawInterval() {
        return get().drawInterval;
    }

    public static Callback<Boolean> scheduleCooldown(String identifier, int cooldown) {
        CooldownCallback callback = new CooldownCallback() {
            protected double localCooldown = cooldown;
            private final String localIdentifier = identifier;

            @Override
            public Boolean call() {
                if (localCooldown < 0) {
                    return true;
                }
                return false;
            }

            @Override
            public void updateCooldown(double dt) {
                localCooldown -= dt;
                if (localCooldown < 0) {
                    unscheduleCooldown(localIdentifier);
                }
            }
        };
        get().scheduledCooldowns.put(identifier, callback);
        return callback;
    }

    public static void unscheduleCooldown(String identifier) {
        get().scheduledCooldowns.remove(identifier);
    }

    private static void updateScheduledCooldowns() {
        double dt = get().delta;
        for (Entry<String, CooldownCallback> entrySet : get().scheduledCooldowns.entrySet()) {
            CooldownCallback callback = entrySet.getValue();
            callback.updateCooldown(dt);
        }
    }
}
