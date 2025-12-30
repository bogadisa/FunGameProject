package secondEngine.components.helpers;

import java.util.Map;

import secondEngine.Config.UIconfig;
import secondEngine.renderer.Font;
import secondEngine.renderer.GlyphMetrics;
import secondEngine.util.AssetPool;

public class Text {
    private String fontPath = "Unifontexmono-lxY45.ttf";
    private Font font;
    private String text;
    private Map<Character, GlyphMetrics> characters;
    private float fontSize;

    private transient boolean isDirty = false;

    public Text() {
        this("!@!#$%!");
    }

    public Text(String text) {
        this(text, UIconfig.getFontSize());
    }

    public Text(String text, float fontSize) {
        this.font = AssetPool.getFont(fontPath);
        setText(text);

        this.fontSize = fontSize;
    }

    public Text setText(String text) {
        this.text = text;
        this.characters = font.getChars(text);
        this.isDirty = true;
        return this;
    }

    public void setClean() {
        this.isDirty = false;
    }

    public String getText() {
        return this.text;
    }

    public Map<Character, GlyphMetrics> getCharacters() {
        return characters;
    }

    public float getFontSize() {
        return fontSize;
    }
}
