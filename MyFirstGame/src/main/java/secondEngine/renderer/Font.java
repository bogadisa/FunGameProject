package secondEngine.renderer;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.util.freetype.FreeType.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_BUFFER;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.*;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_GlyphSlot;

public class Font {
    private Map<Character, GlyphMetrics> characters = new HashMap<>();
    private transient FT_Face face;
    private transient int texID;

    private final int whitespaceWidth = 128;

    public Font init(String filepath) {
        MemoryStack stack = stackPush();
        PointerBuffer pp = stack.mallocPointer(1);
        int err = FT_Init_FreeType(pp);
        if (err != FT_Err_Ok) {
            throw new IllegalStateException("Failed to initialize FreeType: " + FT_Error_String(err));
        }
        long library = pp.get(0);

        IntBuffer major = stack.mallocInt(1);
        IntBuffer minor = stack.mallocInt(1);
        IntBuffer patch = stack.mallocInt(1);

        FT_Library_Version(library, major, minor, patch);
        System.out.println("Loaded FreeType " + major.get(0) + "." + minor.get(0) + "." + patch.get(0));

        err = FT_New_Face(library, filepath, 0, pp);
        long face_adress = pp.get(0);
        this.face = FT_Face.create(face_adress);

        // set the font size
        FT_Set_Pixel_Sizes(face, 0, 32);
        createFontAtlas();

        FT_Done_Face(face);
        FT_Done_FreeType(library);
        return this;
    }

    private Vector2i measureFontAtlas() {
        int width = 0;
        int height = 0;
        for (char c = 0; c < 128; c++) {
            int err = FT_Load_Char(this.face, c, FT_LOAD_RENDER);
            assert err == 0 : "Failed to load " + c + " due to error code: " + err;
            width += face.glyph().bitmap().width();
            height = Math.max(height, face.glyph().bitmap().rows());
        }
        return new Vector2i(width, height);
    }

    private void createFontAtlas() {
        int x = 0;
        Vector2i dimensions = measureFontAtlas();

        this.texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.texID);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1); // disable byte-alignment restriction
        // char c = "h".charAt(0);
        // int err = FT_Load_Char(this.face, c, FT_LOAD_RENDER);
        int bitmapWidth, bitmapRows;
        GlyphMetrics character;
        // bitmapWidth = face.glyph().bitmap().width();
        // bitmapRows = face.glyph().bitmap().rows();
        // character = new GlyphMetrics();
        // characters.put(c, character);
        // character.size = new Vector2i(bitmapWidth, bitmapRows);
        // character.bearing = new Vector2i(face.glyph().bitmap_left(),
        // face.glyph().bitmap_top());
        // // TODO definetly wrong - maybe not if divided by font size
        // character.advance = face.glyph().advance().x() / whitespaceWidth;
        // character.texCoords[0] = new Vector2f(1.0f, 0.0f);
        // character.texCoords[1] = new Vector2f(1.0f, 1.0f);
        // character.texCoords[2] = new Vector2f(0.0f, 1.0f);
        // character.texCoords[3] = new Vector2f(0.0f, 0.0f);
        // int size = bitmapWidth * bitmapRows;
        // ByteBuffer buffer = face.glyph().bitmap().buffer(size);

        // if (c == "h".charAt(0)) {
        // for (int i = 0; i < bitmapRows; i++) {
        // for (int j = 0; j < bitmapWidth; j++) {
        // // System.out.print(buffer.get() > 0 ? 1 : 0);
        // System.out.print(buffer.get() == 0 ? 0 : 1);
        // }
        // // val = buffer.getChar();
        // System.out.println();
        // }
        // System.out.println(c);
        // }
        // buffer.rewind();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, bitmapWidth, bitmapRows, 0, GL_RED,
        // GL_UNSIGNED_BYTE, buffer);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, dimensions.x, dimensions.y, 0, GL_RED, GL_UNSIGNED_BYTE,
                (ByteBuffer) null);

        for (char c = 0; c < 128; c++) {
            // if (c == 32) {
            // continue;
            // }
            int err = FT_Load_Char(this.face, c, FT_LOAD_RENDER);
            assert err == 0 : "Failed to load " + c + " due to error code: " + err;
            FT_GlyphSlot glyph = face.glyph();
            FT_Bitmap bitmap = glyph.bitmap();

            bitmapWidth = bitmap.width();
            bitmapRows = bitmap.rows();

            character = new GlyphMetrics();
            characters.put(c, character);
            character.size = new Vector2i(bitmapWidth, bitmapRows);
            character.bearing = new Vector2i(glyph.bitmap_left(), glyph.bitmap_top());
            // Taken from
            // https://github.com/camdaniel1/Chess3D/blob/ab2b91f7c883a81d1a1d2f236871b3cea8c55aa7/src/main/java/com/github/camdaniel1/render/text/FontLoader.java#L16
            character.advance = glyph.advance().x() >> 6;

            // UV coordinates
            character.texCoords[0] = new Vector2f(((float) x + character.size.x) / dimensions.x, 0f);
            character.texCoords[1] = new Vector2f(((float) x + character.size.x) / dimensions.x,
                    (float) character.size.y / dimensions.y);
            character.texCoords[2] = new Vector2f((float) x / dimensions.x, (float) character.size.y / dimensions.y);
            character.texCoords[3] = new Vector2f((float) x / dimensions.x, 0f);
            // character.uvTopLeft = new Vector2f(x / dimensions.x, 0f);
            // character.uvBottomRight = new Vector2f((x + character.size.x) /
            // dimensions.x,
            // character.size.y / dimensions.y);
            // character.texCoords[0] = new Vector2f(1.0f, 0.0f);
            // character.texCoords[1] = new Vector2f(1.0f, 1.0f);
            // character.texCoords[2] = new Vector2f(0.0f, 1.0f);
            // character.texCoords[3] = new Vector2f(0.0f, 0.0f);

            // System.out.println(bitmap);
            int size = bitmapWidth * bitmapRows;
            // buffer.rewind();
            // FloatBuffer floatBuffer = buffer.asFloatBuffer();
            // if (c == "h".charAt(0)) {
            // for (int i = 0; i < bitmapRows; i++) {
            // for (int j = 0; j < bitmapWidth; j++) {
            // System.out.print(buffer.get() == 0 ? 0 : 1);
            // }
            // System.out.println();
            // }
            // System.out.println(c);
            // buffer.rewind();
            // }
            if (bitmapWidth != 0 || bitmapRows != 0) {
                ByteBuffer buffer = bitmap.buffer(size);
                // ByteBuffer buffer = bitmap.buffer(0);
                // buffer.rewind();
                glTexSubImage2D(GL_TEXTURE_2D, 0, x, 0, bitmapWidth, bitmapRows, GL_RED, GL_UNSIGNED_BYTE, buffer);
                // glTexSubImage2D(this.texID, 0, x, 0, bitmapWidth, bitmapRows, GL_RED,
                // GL_UNSIGNED_BYTE, buffer);
            }

            x += bitmapWidth;
        }
    }

    public GlyphMetrics getMetrics(char c) {
        return characters.get(c);
    }

    public Map<Character, GlyphMetrics> getChars(String text) {
        Set<Character> chars = new HashSet<>();
        for (char c : text.toCharArray()) {
            chars.add(c);
        }
        return getChars(chars);
    }

    public Map<Character, GlyphMetrics> getChars(Set<Character> chars) {
        Map<Character, GlyphMetrics> charInfo = new HashMap<>();
        for (Character c : chars) {
            charInfo.put(c, getMetrics(c));
        }
        return charInfo;
    }

    public int getTexID() {
        return this.texID;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, this.texID);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
