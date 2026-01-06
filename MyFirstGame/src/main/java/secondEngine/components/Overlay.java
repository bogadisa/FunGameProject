package secondEngine.components;

import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;

import secondEngine.Config.UIconfig;
import secondEngine.components.helpers.SlotLayout;
import secondEngine.components.helpers.Sprite;
import secondEngine.components.helpers.TextBox;
import secondEngine.grid.GriddableComponent;
import secondEngine.grid.GriddableSlot;
import secondEngine.grid.SpatialGrid;
import secondEngine.objects.overlay.Layout;
import secondEngine.objects.overlay.Layout.SlotType;

public class Overlay extends GriddableComponent {
    private Sprite corner, edge, fill;
    private boolean ignoreEdge = true;
    private boolean ignoreCorner = true;

    private int UIscale;

    private int height, width;
    private float padding;
    private float scaledHeight, scaledWidth;
    private Vector3f scale;

    private CompositeSpriteRenderer compSprite;
    private List<SlotLayout> slotLayouts;

    private SpatialGrid overlayGrid;

    /**
     * @param width             specifies units of UI scale
     * @param height            -""-
     * @param padding           Specifies the percentage of extra padding per
     *                          internal unit
     * @param sprites           Fill, corner and edge sprite respectively
     * @param individualSprites Wether or not the overlay background should be built
     *                          with individual sprites or not
     * @return
     */
    public Overlay init(int width, int height, float padding, Sprite[] sprites, boolean individualSprites) {
        this.width = width;
        this.height = height;
        this.padding = padding;

        initSprites(sprites);

        this.slotLayouts = new ArrayList<>();

        this.compSprite = this.gameObject.getComponent(CompositeSpriteRenderer.class);
        if (this.compSprite == null) {
            this.compSprite = new CompositeSpriteRenderer().init();
            this.gameObject.addComponent(this.compSprite);
        }
        this.UIscale = UIconfig.getScale();
        this.overlayGrid = new SpatialGrid(this.gameObject.getName(), true);
        // TODO will this mess up because of origin?
        this.overlayGrid.setGridSize((int) (UIscale * (1 + padding)));

        this.scaledWidth = UIscale * width + padding * UIscale * Math.max(width - 2, 0);
        this.scaledHeight = UIscale * height + padding * UIscale * Math.max(height - 2, 0);
        this.scale = new Vector3f(scaledWidth, scaledHeight, 1);
        this.gameObject.transform.scale = this.scale;

        buildBackground(individualSprites);
        compSprite.refreshTextures();

        return this;
    }

    private void initSprites(Sprite[] sprites) {
        fill = sprites[0];
        if (sprites.length > 1) {
            corner = sprites[1];
            ignoreCorner = false;
            if (sprites.length > 2) {
                edge = sprites[2];
                ignoreEdge = false;
            }
        }
    }

    @Override
    public void start() {
        overlayGrid.setName(this.gameObject.getName());
        buildLayout();
    }

    private Sprite getSprite(int i, int j, int x, int y, int[] rotation, boolean[] flip) {
        Sprite piece = fill;
        if (j == 0) {
            rotation[0] = 180;

            if (!ignoreEdge) {
                piece = edge;
            }
            if (i == 0 || i == x - 1) {
                if (!ignoreCorner) {
                    piece = corner;
                    if (i == 0) {
                        flip[0] = true;
                    }
                }
                // else {
                // rotation[0] = 0;
                // }
            }
        } else if (j == y - 1) {
            if (!ignoreEdge) {
                piece = edge;
            }
            if (i == 0 || i == x - 1) {
                if (!ignoreCorner) {
                    piece = corner;
                    if (i == x - 1) {
                        flip[0] = true;
                    }
                }
            }
        } else {
            if (i == 0 || i == x - 1) {
                if (!ignoreEdge) {
                    piece = edge;
                    rotation[0] = 90;
                    if (i == x - 1) {
                        flip[0] = true;
                    }
                }
            }
        }
        return piece;
    }

    private void buildBackground(boolean individualSprites) {
        // TODO should this all be shifted a bit to the left?
        Vector3f scale, offset;
        int[] rotation = { 0 };
        boolean[] flip = { false };
        int nSpritesX = 3;
        int nSpritesY = 3;
        if (individualSprites) {
            nSpritesX = width;
            nSpritesY = height;
        }

        Sprite piece = fill;
        for (int i = 0; i < nSpritesX; i++) {
            for (int j = 0; j < nSpritesY; j++) {
                rotation[0] = 0;
                flip[0] = false;
                piece = getSprite(i, j, nSpritesX, nSpritesY, rotation, flip);
                scale = getScale(i, j, individualSprites);
                if (individualSprites) {
                    offset = getOffset(i, j);
                } else {
                    offset = getOffset(0.5f * i * (width - 1), 0.5f * j * (height - 1));
                }
                compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(piece), scale, offset, rotation[0],
                        flip[0]);
            }
        }
        // scale = new Vector3f(scaledWidth - 2 * UIscale, scaledHeight - 2 * UIscale,
        // 1);
        // offset = new Vector3f(scaledWidth / 2, scaledHeight / 2, 0);
        // compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(fill), scale,
        // offset, rotation, flip);
    }

    public <Slot extends GriddableSlot> boolean addSlot(Slot slot) {
        Vector3f scale = new Vector3f(UIscale, UIscale, 1);
        for (SlotLayout slotLayout : slotLayouts) {
            Layout layout = slotLayout.layout;
            SlotType[][] slots = layout.getLayout();
            for (int i = 0; i < layout.getScale().x; i++) {
                for (int j = 0; j < layout.getScale().y; j++) {
                    if (slots[j][i] == slot.slotType && !layout.isOccupied(i, j)) {
                        Vector3f offset = getOffset(i, j, 2);
                        slot.setOffset(offset);
                        slot.setScale(scale);
                        layout.setOccupied(i, j, true);
                        overlayGrid.addObject(slot);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void addLayout(Layout layout, Map<Layout.SlotType, Sprite> sprites) {
        slotLayouts.add(new SlotLayout(layout, sprites));
    }

    private Vector3f getScale(float i, float j, boolean individualSprites) {
        float xScale, yScale;
        if (i == 0) {
            xScale = UIscale;
        } else {
            if (individualSprites) {
                if (i == width - 1) {
                    xScale = UIscale;
                } else {
                    xScale = UIscale * (1 + padding);
                }
            } else {
                if (i == 2) {
                    xScale = UIscale;
                } else {
                    xScale = scaledWidth - 2 * UIscale;
                }
            }
        }

        if (j == 0) {
            yScale = UIscale;
        } else {
            if (individualSprites) {
                if (j == height - 1) {
                    yScale = UIscale;
                } else {
                    yScale = UIscale * (1 + padding);
                }
            } else {
                if (j == 2) {
                    yScale = UIscale;
                } else {
                    yScale = scaledHeight - 2 * UIscale;
                }
            }
        }

        return new Vector3f(xScale, yScale, 1);
    }

    private Vector3f getOffset(float i, float j) {
        return getOffset(i, j, 1);
    }

    private Vector3f getOffset(float i, float j, int zIndexOffset) {
        float x = 0.5f * UIscale;
        float y = 0.5f * UIscale;
        if (i == width - 1) {
            x = scaledWidth - x;
        } else if (i != 0) {
            x += UIscale * (1 + padding) * (i - 1);
            x += UIscale * (1 + 0.5f * padding);
        }
        if (j == height - 1) {
            y = scaledHeight - y;
        } else if (j != 0) {
            y += UIscale * (1 + padding) * (j - 1);
            y += UIscale * (1 + 0.5f * padding);
        }

        return new Vector3f(x, y, zIndexOffset);
    }

    private void buildLayout() {
        for (int i = 0; i < slotLayouts.size(); i++) {
            SlotLayout slotLayout = slotLayouts.get(i);
            if (!slotLayout.initialized) {
                buildLayout(slotLayout);
                slotLayout.initialized = true;
            }
        }
    }

    private void buildLayout(SlotLayout slotLayout) {
        SlotType[][] slots = slotLayout.layout.getLayout();

        Vector3f scale, offset;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                scale = new Vector3f(UIscale, UIscale, 1);
                offset = getOffset(i, j);

                SlotType slotType = slots[j][i];
                if (slotType != SlotType.Other.NULL) {
                    if (slotType.isOfType(SlotType.Text.class)) {
                        addTextSlot(scale, offset, slotType);
                    } else {
                        addSlot(compSprite, scale, offset, slotType, slotLayout.layoutSprites);
                    }
                }
            }
        }
    }

    private void addSlot(CompositeSpriteRenderer compSprite, Vector3f scale, Vector3f offset, SlotType slotType,
            Map<Layout.SlotType, Sprite> sprites) {
        if (slotType.isOfType(SlotType.Other.class)) {
            return;
        }
        if (slotType == SlotType.Interactable.INVENTORY) {
            SpriteRenderer sprite = new SpriteRenderer().setSprite(sprites.get(slotType));
            compSprite.addSpriteRenderer(sprite, new Vector3f(scale), new Vector3f(offset).add(0, 0, 1));
        }
    }

    private void addTextSlot(Vector3f scale, Vector3f offset, SlotType slotType) {
        if (slotType == SlotType.Text.POS) {
            TextRenderer text = this.gameObject.getComponent(TextRenderer.class);
            if (text == null) {
                text = new TextRenderer();
            }
            TextBox posTextBox = new TextBox(100, 100, offset);
            posTextBox.setName("pos");
            text.addTextBox(posTextBox);
        }
    }

    public SpatialGrid getOverlayGrid() {
        return overlayGrid;
    }

    @Override
    public Vector3f getPosition() {
        return this.gameObject.getPosition();
    }

    @Override
    public Vector3f getScale() {
        return this.scale;
    }

    @Override
    public int getObjectId() {
        return this.hashCode();
    }

    @Override
    public void update(float dt) {
    }

}
