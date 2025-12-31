package secondEngine.components;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;

import secondEngine.Component;
import secondEngine.components.helpers.TextBox;
import secondEngine.components.helpers.TextBox.CharacterInfo;
import secondEngine.renderer.BatchRendererFont;

public class TextRenderer extends Component {
    private Vector4f color = new Vector4f(1, 1, 0, 1);
    private List<TextBox> textBoxes = new ArrayList<>();

    private transient Transform lastTransform;

    private transient boolean isDirty = true;

    private BatchRendererFont renderer;
    private boolean addedToRenderer;

    @Override
    public void start() {
        refreshText();

        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(float dt) {
        for (TextBox box : textBoxes) {
            box.updateText();
        }
        if (!this.lastTransform.equals(gameObject.transform)) {
            gameObject.transform.copy(lastTransform);
            isDirty = true;
        }
    }

    public void refreshText() {
        for (TextBox box : textBoxes) {
            box.refreshText();
        }
    }

    public Vector4f getColor() {
        return this.color;
    }

    public TextRenderer setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.isDirty = true;
            this.color = color;
        }

        return this;
    }

    public List<TextBox> getTextBoxes() {
        return this.textBoxes;
    }

    public TextRenderer addTextBox(TextBox textBox) {
        this.textBoxes.add(textBox);
        this.isDirty = true;
        return this;
    }

    public List<CharacterInfo> getCharacters() {
        List<CharacterInfo> characters = new ArrayList<>();
        for (TextBox box : textBoxes) {
            characters.addAll(box.getCharacters());
        }
        return characters;
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public void setClean() {
        this.isDirty = false;
    }

    public boolean isAddedToRenderer() {
        return addedToRenderer;
    }

    public void setAddedToRenderer(boolean addedToRenderer, BatchRendererFont renderer) {
        this.addedToRenderer = addedToRenderer;
        this.renderer = renderer;
    }
}
