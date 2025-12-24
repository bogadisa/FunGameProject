package secondEngine.components;

import java.util.Map;

import org.joml.Vector3f;

import secondEngine.Component;
import secondEngine.Config.UIconfig;
import secondEngine.Window;
import secondEngine.components.helpers.InventorySlot;
import secondEngine.components.helpers.Sprite;
import secondEngine.grid.SpatialGrid;
import secondEngine.grid.GriddableComponent;
import secondEngine.objects.GameObject;
import secondEngine.objects.overlay.Layout;
import secondEngine.objects.overlay.Layout.SlotType;

public class Overlay extends GriddableComponent {
    private final int OVERLAY_Z_INDEX = 2;
    private int UIscale;
    private boolean rescale = false;

    private Sprite corner, edge, fill;
    private boolean ignoreEdge = true;
    private boolean ignoreCorner = true;

    private int height, width;
    private Vector3f scale;

    private Layout layout;
    private Map<Layout.SlotType, Sprite> layoutSprites;
    private SpatialGrid overlayGrid;

    private transient boolean isInitialized = false;

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

    /**
     * Uses the game object UIscale transform for background, where layout <= 0,
     * otherwise it doubles (quadruple area) the size
     * 
     * @param go            Needed for adding the composite sprite
     * @param sprites       The background
     * @param layoutSprites Sprites to use when layout[i][j] > 0
     * @param layout        A NxM matrix where layout[i][j]=0 does nothing,
     *                      otherwise layoutSprites[layout[i][j]-1]
     * @return this
     */
    public Overlay init(GameObject go, Sprite[] sprites, Map<Layout.SlotType, Sprite> layoutSprites, Layout layout) {
        this.layout = layout;
        this.layoutSprites = layoutSprites;
        int width = layout.getLayout()[0].length;
        int height = layout.getLayout().length;
        return init(go, sprites, width, height);
    }

    /**
     * 
     * @param go      Needed for adding the composite sprite
     * @param sprites The background
     * @param width
     * @param height
     * @return this
     */
    public Overlay init(GameObject go, Sprite[] sprites, int width, int height) {
        // TODO does this need to be initialized in another way?
        this.overlayGrid = new SpatialGrid(go.getName(), true);
        this.initSprites(sprites);
        this.gameObject = go;
        this.height = height;
        this.width = width;
        this.UIscale = UIconfig.getScale();
        this.scale = new Vector3f(width * UIscale, height * UIscale, 1);
        buildCompositeSprite();
        isInitialized = true;
        return this;
    }

    private Overlay buildCompositeSprite() {
        Layout.SlotType[][] layout;
        if (this.layout != null) {
            layout = this.layout.getLayout();
        } else {
            layout = new Layout.SlotType[height][width];
        }
        // TODO might cause issues if a comp sprite already exists
        CompositeSpriteRenderer compSprite = this.gameObject.getComponent(CompositeSpriteRenderer.class);
        if (compSprite == null) {
            compSprite = new CompositeSpriteRenderer().init();
            this.gameObject.addComponent(compSprite);
        }
        int halfWidth = width / 2;
        int halfHeight = height / 2;

        for (int i = 1; i <= width; i++) {
            for (int j = 1; j <= height; j++) {
                int[] rotation = { 0 };
                boolean[] flip = { false };

                Sprite piece = getSprite(i, j, width, height, rotation, flip);
                Vector3f offset = new Vector3f((i - 1 - halfWidth) * UIscale, (j - 1 - halfHeight) * UIscale,
                        OVERLAY_Z_INDEX);
                Vector3f scale = new Vector3f(UIscale, UIscale, 1);
                compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(piece), scale, offset, rotation[0],
                        flip[0]);
                Layout.SlotType slotType = layout[height - j][i - 1];
                if (slotType != null) {
                    addSlot(compSprite, scale, offset, slotType);
                }
            }
        }

        return this;

    }

    private void addSlot(CompositeSpriteRenderer compSprite, Vector3f scale, Vector3f offset, SlotType slotType) {
        if (slotType.isOfType(SlotType.Other.class)) {
            return;
        }
        compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(layoutSprites.get(slotType)), new Vector3f(scale),
                new Vector3f(offset));
        if (slotType == SlotType.Interactable.INVENTORY) {
            Inventory inventory = this.gameObject.getComponent(Inventory.class);
            SpriteRenderer sprite = new SpriteRenderer();
            InventorySlot slot = inventory.addSpriteRenderer(sprite);
            slot.setOffset(offset);
            overlayGrid.addObject(slot);
            compSprite.addSpriteRenderer(sprite, new Vector3f(scale), new Vector3f(offset).add(0, 0, 1));
        }
    }

    private Sprite getSprite(int i, int j, int x, int y, int[] rotation, boolean[] flip) {
        Sprite piece = fill;
        if (j == 1) {
            rotation[0] = 180;

            if (!ignoreEdge) {
                piece = edge;
            }
            if (i == 1 || i == x) {
                if (!ignoreCorner) {
                    piece = corner;
                    if (i == 1) {
                        flip[0] = true;
                    }
                } else {
                    rotation[0] = 0;
                }
            }
        } else if (j == y) {
            if (!ignoreEdge) {
                piece = edge;
            }
            if (i == 1 || i == x) {
                if (!ignoreCorner) {
                    piece = corner;
                    if (i == x) {
                        flip[0] = true;
                    }
                }
            }
        } else {
            if (i == 1 || i == x) {
                if (!ignoreEdge) {
                    piece = edge;
                    rotation[0] = 90;
                    if (i == x) {
                        flip[0] = true;
                    }
                }
            }
        }
        return piece;
    }

    @Override
    public void start() {
        overlayGrid.setName(this.gameObject.getName());
        // Window.getScene().worldGrid().addObject(this.gameObject);
        // for (Griddable obj : linkedObjects) {
        // overlayGrid.addObject(obj);
        // }
        assert isInitialized
                : "The game component " + this.gameObject.getName() + " has not initialized its overlay component";
    }

    @Override
    public void update(float dt) {
        // if (this.gameObject.getName().equals("inventoryObj")) {
        // float xSpeed = 2f * dt;
        // float ySpeed = 1f * dt;
        // this.gameObject.transform.position.add(xSpeed, ySpeed, 0);
        // SpatialGrid grid = Window.getScene().worldGrid();
        // grid.updateObject(this.gameObject);
        // }
        // // TODO remove because better permanent system is needed
        // for (GameObject go : linkedObjects) {
        // GridMachine gm = go.getComponent(GridMachine.class);
        // if (gm.isDirty()) {
        // GridState gs = gm.getGridState(overlayGrid.getName());
        // AnimationStateMachine sm =
        // gameObject.getComponent(AnimationStateMachine.class);
        // if (sm != null && gm.highlight()) {
        // Set<String> currentGridCells = gs.getCurrentGridCells();
        // Set<String> differenceGridCells = gs.getDifferenceGridCells();
        // if (currentGridCells != null) {
        // for (String pos : currentGridCells) {
        // int spriteRendererIndex = spriteRenderers.getOrDefault(pos, -1);
        // if (spriteRendererIndex > -1) {
        // sm.trigger("addColor", spriteRendererIndex);
        // }
        // }
        // }
        // for (String lastPos : differenceGridCells) {
        // int spriteRendererIndex = spriteRenderers.getOrDefault(lastPos, -1);
        // if (spriteRendererIndex > -1) {
        // sm.trigger("removeColor", spriteRendererIndex);
        // }
        // }
        // }
        // }
        // }
    }

    public SpatialGrid getOverlayGrid() {
        return this.overlayGrid;
    }

    public Overlay addBackdrop() {
        // TODO not sure what this is for
        return this;
    }

    /**
     * Can remake and rescale the overlay.
     * 
     * Remake: The composite sprite of the overlay is dependent on the size of the
     * window E.g the grid
     * 
     * Rescale: The composite sprite pieces' UIscale is dependent on the size of the
     * window E.g UI stuff
     */
    public void resize() {
        if (rescale) {

        }
    }

    public void setRescale(boolean rescale) {
        this.rescale = rescale;
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
}
