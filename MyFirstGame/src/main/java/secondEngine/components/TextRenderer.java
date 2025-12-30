package secondEngine.components;

import java.util.List;

import org.joml.Vector4f;

import secondEngine.Component;
import secondEngine.components.helpers.TextBox;
import secondEngine.components.helpers.TextBox.CharacterInfo;
import secondEngine.renderer.BatchRendererFont;

public class TextRenderer extends Component {
    private Vector4f color = new Vector4f(1, 1, 0, 1);
    private TextBox textBox = new TextBox(0, 0);

    private transient Transform lastTransform;

    private boolean isHidden = false;
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

        refreshText();
        if (!this.lastTransform.equals(gameObject.transform)) {
            gameObject.transform.copy(lastTransform);
            isDirty = true;
        }
    }

    public void refreshText() {
        this.textBox.refreshText();
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

    public TextBox getTextBox() {
        return this.textBox;
    }

    public TextRenderer setTextBox(TextBox textBox) {
        this.textBox = textBox;
        this.isDirty = true;
        return this;
    }

    public List<CharacterInfo> getCharacters() {
        return this.textBox.getCharacters();
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
