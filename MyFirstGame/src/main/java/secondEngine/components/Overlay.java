package secondEngine.components;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import secondEngine.Component;
import secondEngine.Window;
import secondEngine.Config.UIconfig;
import secondEngine.components.helpers.Sprite;
import secondEngine.components.helpers.GridState;
import secondEngine.components.helpers.Interactable;
import secondEngine.components.helpers.OverlayState;
import secondEngine.objects.GameObject;
import secondEngine.util.PrefabFactory;

public class Overlay extends Component {
    private boolean rescale = false;

    private Sprite corner, edge, fill, inventory;
    private boolean ignoreEdge = true;
    private boolean ignoreCorner = true;
    private Sprite[] layoutSprites;

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
     * Uses the game object scale transform for background, where layout <= 0, otherwise it doubles (quadruple area) the size
     * @param go Needed for adding the composite sprite
     * @param sprites The background
     * @param layoutSprites Sprites to use when layout[i][j] > 0
     * @param layout A NxM matrix where layout[i][j]=0 does nothing, otherwise layoutSprites[layout[i][j]-1]
     * @return this
     */
    public Overlay init(GameObject go, Sprite[] sprites, Sprite[] layoutSprites, int[][] layout) {
        this.initSprites(sprites);
        this.layoutSprites = layoutSprites;
        this.gameObject = go;
        this.buildCompositeSprite(layout);
        isInitialized = true;
        return this;

    }

    /**
     * 
     * @param go Needed for adding the composite sprite
     * @param sprites The background
     * @param numSpritesX
     * @param numSpritesY
     * @return this
     */
    public Overlay init(GameObject go, Sprite[] sprites, int numSpritesX, int numSpritesY) {
        this.initSprites(sprites);
        this.gameObject = go;
        this.buildCompositeSprite(numSpritesX, numSpritesY);
        isInitialized = true;
        return this;
    }

    private Overlay buildCompositeSprite(int numSpritesX, int numSpritesY) {
        CompositeSpriteRenderer compSprite = new CompositeSpriteRenderer().init();
        int scale = UIconfig.getScale();
        for (int i = 1; i <= numSpritesX; i++) {
            for (int j = 1; j <= numSpritesY; j++) {
                int[] rotation = {0};
                boolean[] flip = {false};

                Sprite piece = addSprite(i, j, numSpritesX, numSpritesY, rotation, flip);
                compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(piece), new Vector3f((i - 1) * scale, (j - 1) * scale, 2), rotation[0], flip[0]);
            }
        }
        
        this.gameObject.addComponent(compSprite);
        return this;
    }

    private Overlay buildCompositeSprite(int[][] layout) {
        int sizeY = layout.length;
        int sizeX = layout[0].length;
        CompositeSpriteRenderer compSprite = new CompositeSpriteRenderer().init();
        int scale = UIconfig.getScale();

        int specialPiecesAdded = 0;
        for (int i = 1; i <= sizeX; i++) {
            for (int j = 1; j <= sizeY; j++) {
                int[] rotation = {0};
                boolean[] flip = {false};

                Sprite piece = addSprite(i, j, sizeX, sizeY, rotation, flip);
                compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(piece), new Vector3f((i - 1) * scale, (j - 1) * scale, 2), rotation[0], flip[0]);
                if (layout[sizeY-j][i-1] > 0) {
                    Vector3f offset =  new Vector3f((i - 1) * scale, (j - 1) * scale, 2);
                    compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(layoutSprites[layout[sizeY-j][i-1] - 1]), offset);
                    if (specialPiecesAdded < 15) {
                        Sprite sprite = PrefabFactory.getObjectSprite(1000 + specialPiecesAdded);
                        compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(sprite), offset);
                        
                        Vector3f worldPos = new Vector3f().set(this.gameObject.transform.position);
                        worldPos.add(offset);
                        Vector2f screenPos = Window.getScene().camera().worldToScreen(worldPos);

                        // TODO not serializable
                        Interactable temp = new Interactable() {
                            @Override
                            public void interact() {
                                CompositeSpriteRenderer compSprite = gameObject.getComponent(CompositeSpriteRenderer.class);
                                // hmmm
                            }
                            
                        };
                        temp.setActive(true);
                        temp.gameObject = this.gameObject;
                        OverlayState.addInteractable(temp, screenPos);
                    }
                    specialPiecesAdded++;
                }
            }
        }
        
        this.gameObject.addComponent(compSprite);
        return this;

    }

    private Sprite addSprite(int i, int j, int x, int y, int[] rotation, boolean[] flip) {
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
            };
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
            };
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
        assert isInitialized : "The game component " + this.gameObject.getName() + " has not initialized its overlay component";
    }

    @Override
    public void update(float dt) {
    }

    public Overlay addBackdrop() {
        // TODO not sure what this is for
        return this;
    }

    /**
     * Can remake and rescale the overlay.
     * 
     * Remake:
     *      The composite sprite of the overlay is dependent on the size of the window
     *      E.g the grid
     * 
     * Rescale:
     *      The composite sprite pieces' scale is dependent on the size of the window
     *      E.g UI stuff
     */
    public void resize() {
        if (rescale) {

        }
    }

    public void setRescale(boolean rescale) {
        this.rescale = rescale;
    }
}
