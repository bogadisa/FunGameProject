package secondEngine.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joml.Vector3f;

import secondEngine.Component;
import secondEngine.Window;
import secondEngine.Config.UIconfig;
import secondEngine.components.helpers.Sprite;
import secondEngine.grid.GridState;
import secondEngine.grid.GridableObject;
import secondEngine.grid.Griddable;
import secondEngine.grid.GriddableComponent;
import secondEngine.grid.SpatialGrid;
import secondEngine.objects.GameObject;
import secondEngine.objects.overlay.Layout;
import secondEngine.objects.overlay.Layout.SlotType;
import secondEngine.util.PrefabFactory;

public class Overlay extends Component {
    private final int OVERLAY_Z_INDEX = 2;
    private boolean rescale = false;

    private Sprite corner, edge, fill;
    private boolean ignoreEdge = true;
    private boolean ignoreCorner = true;

    private Layout layout;
    private Map<Layout.SlotType, Sprite> layoutSprites;
    private SpatialGrid overlayGrid;
    private List<Griddable> linkedObjects;

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
     * Uses the game object scale transform for background, where layout <= 0,
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
        int numSpritesX = layout.getLayout()[0].length;
        int numSpritesY = layout.getLayout().length;
        return init(go, sprites, numSpritesX, numSpritesY);
    }

    /**
     * 
     * @param go          Needed for adding the composite sprite
     * @param sprites     The background
     * @param numSpritesX
     * @param numSpritesY
     * @return this
     */
    public Overlay init(GameObject go, Sprite[] sprites, int numSpritesX, int numSpritesY) {
        // TODO does this need to be initialized in another way?
        this.overlayGrid = new SpatialGrid(go.getName(), true);
        this.initSprites(sprites);
        this.gameObject = go;
        this.linkedObjects = new ArrayList<>();
        this.buildCompositeSprite(numSpritesX, numSpritesY);
        isInitialized = true;
        return this;
    }

    private Overlay buildCompositeSprite(int numSpritesX, int numSpritesY) {
        Layout.SlotType[][] layout;
        if (this.layout != null) {
            layout = this.layout.getLayout();
        } else {
            layout = new Layout.SlotType[numSpritesY][numSpritesX];
        }
        return buildCompositeSprite(numSpritesX, numSpritesY, layout);

    }

    // TODO remove this testing stuff
    int specialPiecesAdded = 0;

    private Overlay buildCompositeSprite(int numSpritesX, int numSpritesY, Layout.SlotType[][] layout) {
        // TODO might cause issues if a comp sprite already exists
        CompositeSpriteRenderer compSprite = new CompositeSpriteRenderer().init();
        int scale = UIconfig.getScale();

        specialPiecesAdded = 0;
        for (int i = 1; i <= numSpritesX; i++) {
            for (int j = 1; j <= numSpritesY; j++) {
                int[] rotation = { 0 };
                boolean[] flip = { false };

                Sprite piece = getSprite(i, j, numSpritesX, numSpritesY, rotation, flip);
                Vector3f offset = new Vector3f((i - 1) * scale, (j - 1) * scale, OVERLAY_Z_INDEX);
                compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(piece), offset, rotation[0], flip[0]);
                Layout.SlotType slotType = layout[numSpritesY - j][i - 1];
                if (slotType != null && !slotType.isOfType(Layout.SlotType.Other.class)) {
                    if (layoutSprites != null) {
                        addSpecialSprite(compSprite, offset, slotType);
                    }

                }
            }
        }

        this.gameObject.addComponent(compSprite);
        return this;

    }

    private void addSpecialSprite(CompositeSpriteRenderer compSprite, Vector3f offset, SlotType slotType) {
        compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(layoutSprites.get(slotType)), new Vector3f(offset));

        PrefabFactory.PrefabIds.GroundPrefabs.Spring enumSpring = PrefabFactory
                .getEnum(PrefabFactory.PrefabIds.GroundPrefabs.Spring.class, specialPiecesAdded);
        if (enumSpring != null) {
            Sprite sprite = PrefabFactory.getObjectSprite(enumSpring);
            SpriteRenderer spriteRenderer = new SpriteRenderer().setSprite(sprite);
            compSprite.addSpriteRenderer(spriteRenderer, new Vector3f(offset));

            if (slotType.isOfType(Layout.SlotType.Interactable.class)) {
                this.linkedObjects.add(spriteRenderer);
            }
        }
        specialPiecesAdded++;

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
            ;
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
            ;
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
        for (Griddable obj : linkedObjects) {
            overlayGrid.addObject(obj);
        }
        assert isInitialized
                : "The game component " + this.gameObject.getName() + " has not initialized its overlay component";
    }

    @Override
    public void update(float dt) {
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
     * Rescale: The composite sprite pieces' scale is dependent on the size of the
     * window E.g UI stuff
     */
    public void resize() {
        if (rescale) {

        }
    }

    public void setRescale(boolean rescale) {
        this.rescale = rescale;
    }
}
