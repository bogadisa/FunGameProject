package secondEngine;

import static org.lwjgl.opengl.GL11.glViewport;

import secondEngine.Config.CameraConfig;

public class Config {
    public class CameraConfig {
        // Will be initialized by window
        public static int width;
        public static int height;

        public static int resWidth = 1920;
        public static int resHeight = 1080;
    }

    public class UIconfig {
        private static int scale = 32;
        private static int fontScale = 32;
        private static float defaultFontSize = 0.5f;
        public static float scalePercentage = 1;

        public static int getScale() {
            return (int) (scalePercentage * scale);
        }

        /**
         * @return Font size in pixels
         */
        public static float getDefaultFontSize() {
            return defaultFontSize * scale / fontScale;
        }

        public static int getFontScale() {
            return fontScale;
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
