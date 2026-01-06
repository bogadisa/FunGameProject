package secondEngine.components.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.joml.Vector2f;
import org.joml.Vector3f;

import secondEngine.Config.UIconfig;
import secondEngine.renderer.GlyphMetrics;

public class TextBox {
    public class CharacterInfo {
        public char c;
        public Vector3f pos;
        public Vector2f scale;
        public GlyphMetrics metrics;

        private CharacterInfo(char c, Vector3f pos, Vector2f scale, GlyphMetrics metrics) {
            this.c = c;
            this.pos = pos;
            this.scale = scale;
            this.metrics = metrics;
        }
    }

    private Optional<String> name = Optional.empty();

    private int width, height;
    private float x = 0;
    private float y = 0;
    private Vector3f offset;
    private transient List<CharacterInfo> characters;

    private transient boolean isDirty = false;
    private List<Text> texts;

    public TextBox(int width, int height) {
        this(width, height, new Vector3f(0));
    }

    public TextBox(int width, int height, Vector3f offset) {
        this.width = width;
        this.height = height;

        this.offset = offset;

        this.characters = new ArrayList<>();
        this.texts = new ArrayList<>();
    }

    public TextBox setName(String name) {
        this.name = Optional.of(name);
        return this;
    }

    public Optional<String> getName() {
        return name;
    }

    public TextBox setText(String text) {
        return setText(new Text(text));
    }

    public TextBox setText(Text text) {
        texts.clear();
        texts.add(text);
        refreshText();
        return this;
    }

    public TextBox addText(String text) {
        return addText(new Text(text));
    }

    public TextBox addText(Text text) {
        String textString = text.getText();
        Map<Character, GlyphMetrics> chars = text.getCharacters();
        float fontSize = text.getFontSize();
        for (char c : textString.toCharArray()) {
            GlyphMetrics metric = chars.get(c);
            float advance = metric.advance * fontSize;
            if (x + advance > width) {
                x = 0;
                // TODO how to deal with overflow?
                y -= UIconfig.getScale() * fontSize * 1.1;
            }
            Vector3f pos = new Vector3f(x + metric.bearing.x * fontSize,
                    y - (metric.size.y - metric.bearing.y) * fontSize, 0).add(this.offset);

            Vector2f scale = new Vector2f(metric.size).mul(fontSize);
            pos.add(new Vector3f(scale, 0).mul(0.5f));
            CharacterInfo charInfo = new CharacterInfo(c, pos, scale, metric);
            this.characters.add(charInfo);
            x += advance;
        }
        this.texts.add(text);

        return this;
    }

    public TextBox refreshText() {
        // TODO optimize this so that it isn't updated every frame, using dirty flags
        Text[] texts = this.texts.toArray(new Text[this.texts.size()]);
        this.x = 0;
        this.y = 0;
        this.texts.clear();
        this.characters.clear();
        for (Text text : texts) {
            text.setText(text.getText());
            this.addText(text);
        }
        this.isDirty = false;
        return this;
    }

    public void updateText() {
        if (!isDirty) {
            for (Text text : this.texts) {
                if (text.isDirty()) {
                    isDirty = true;
                    break;
                }
            }
        }
        if (!isDirty) {
            return;
        }
        refreshText();
    }

    public void setOffset(Vector3f offset) {
        this.offset = offset;
        this.isDirty = true;
    }

    public List<CharacterInfo> getCharacters() {
        return characters;
    }
}
