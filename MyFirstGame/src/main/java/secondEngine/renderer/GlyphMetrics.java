package secondEngine.renderer;

import org.joml.Vector2f;
import org.joml.Vector2i;

public class GlyphMetrics {
    public Vector2i size;
    public Vector2i bearing;
    public long advance;

    public Vector2f[] texCoords = new Vector2f[4];
}
