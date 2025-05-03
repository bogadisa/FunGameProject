package secondEngine;

public class Config {
    public class CameraConfig {
        public static int width = 1920;
        public static int height = 1080;

        public static void setResolution(int width, int height) {
            CameraConfig.width = width;
            CameraConfig.height = height;
        }
    }

    public class UIconfig {
        private static int scale = 32;
        public static float scalePercentage = 1;

        public static int getScale() {
            return (int) scalePercentage * scale;
        }
    }

    private static Config config = null;
    public static CameraConfig camera = null;
    public static UIconfig ui = null;

    public static Config get() {
        if (Config.config == null) {
            Config.config = new Config();

        }
        return Config.config;
    }

    public static CameraConfig getCameraConfig() {
        if (Config.config == null) {
            Config.config = new Config();

        }
        if (Config.camera == null) {
            Config.camera = Config.config.new CameraConfig();
        }

        return Config.camera;
    }

    public static UIconfig getUIconfig() {
        if (Config.config == null) {
            Config.config = new Config();

        }
        if (Config.ui == null) {
            Config.ui = Config.config.new UIconfig();
        }

        return Config.ui;
    }
}
