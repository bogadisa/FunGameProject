package secondEngine.components.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joml.Vector2f;

import secondEngine.renderer.GlyphMetrics;

public class TextBox {
    public class CharacterInfo {
        public char c;
        public Vector2f pos;
        public Vector2f scale;
        public GlyphMetrics metrics;

        private CharacterInfo(char c, Vector2f pos, Vector2f scale, GlyphMetrics metrics) {
            this.c = c;
            this.pos = pos;
            this.scale = scale;
            this.metrics = metrics;
        }
    }

    private int width, height;
    private float x = 0;
    private float y = 0;
    private transient List<CharacterInfo> characters;

    private List<Text> text;

    public TextBox(int width, int height) {
        this.width = width;
        this.height = height;

        this.characters = new ArrayList<>();
        this.text = new ArrayList<>();
    }

    public TextBox addText(Text text) {
        String textString = text.getText();
        Map<Character, GlyphMetrics> chars = text.getCharacters();
        float fontSize = text.getFontSize();
        for (char c : textString.toCharArray()) {
            GlyphMetrics metric = chars.get(c);
            Vector2f pos = new Vector2f(x + metric.bearing.x * fontSize,
                    y - (metric.size.y - metric.bearing.y) * fontSize);

            Vector2f scale = new Vector2f(metric.size).mul(fontSize);
            pos.add(new Vector2f(scale).mul(0.5f));
            CharacterInfo charInfo = new CharacterInfo(c, pos, scale, metric);
            this.characters.add(charInfo);
            x += metric.advance;
        }
        this.text.add(text);

        return this;
    }

    public TextBox refreshText() {
        // TODO optimize this so that it isn't updated every frame, using dirty flags
        Text[] texts = this.text.toArray(new Text[this.text.size()]);
        this.x = 0;
        this.text.clear();
        this.characters.clear();
        for (Text text : texts) {
            text.setText(text.getText());
            this.addText(text);
        }
        return this;
    }

    public List<CharacterInfo> getCharacters() {
        return characters;
    }
}
