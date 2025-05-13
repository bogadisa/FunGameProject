package secondEngine.components;

import org.joml.Vector2i;
import org.joml.Vector3f;

import secondEngine.Component;
import secondEngine.Config.UIconfig;
import secondEngine.components.helpers.GridState;
import secondEngine.components.helpers.Sprite;
import secondEngine.objects.GameObject;

public class Overlay extends Component {
    private boolean rescale = false;

    private Sprite corner, edge, fill, inventory;

    private transient boolean isInitialized = false;


    public Overlay init(GameObject go, Sprite[] sprites, int numSpritesX, int numSpritesY) {
        corner = sprites[0];
        edge = sprites[1];
        fill = sprites[2];
        inventory = sprites[3];
        this.gameObject = go;
        this.buildCompositeSprite(numSpritesX, numSpritesY);
        isInitialized = true;
        return this;
    }

    private Overlay buildCompositeSprite(int numSpritesX, int numSpritesY) {
        CompositeSpriteRenderer compSprite = new CompositeSpriteRenderer().init();
        boolean ignoreCorner = false;
        boolean ignoreEdge = false;
        boolean ignoreInventory = false;
        if (corner.equals(fill)) {
            ignoreCorner = true;
        }
        if (edge.equals(fill)) {
            ignoreEdge = true;
        }
        if (inventory.equals(fill)) {
            ignoreInventory = true;
        }
        int scale = UIconfig.getScale();
        for (int i = 1; i <= numSpritesX; i++) {
            for (int j = 1; j <= numSpritesY; j++) {
                Sprite piece = fill;
                int rotation = 0;
                boolean flip = false;
                if (j == 1) {
                    piece = edge;
                    rotation = 180;
                    if (i == 1 || i == numSpritesX) {
                        if (!ignoreCorner) {
                            piece = corner;
                            if (i == 1) {
                                flip = true;
                            }
                        } else {
                            rotation = 0;
                        }
                    };
                } else if (j == numSpritesY) {
                    piece = edge;
                    if (i == 1 || i == numSpritesX) {
                        if (!ignoreCorner) {
                            piece = corner;
                            if (i == numSpritesX) {
                                flip = true;
                            }
                        }
                    };
                } else {
                    if (i == 1 || i == numSpritesX) {
                        if (!ignoreEdge) {
                            piece = edge;
                            rotation = 90;
                            if (i == numSpritesX) {
                                flip = true;
                            }
                        }
                    } else {
                        if (!ignoreInventory) {
                            compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(inventory), new Vector3f((i - 1) * scale, (j - 1) * scale, 3), rotation, flip);
                        }
                    }
                }
    
                compSprite.addSpriteRenderer(new SpriteRenderer().setSprite(piece), new Vector3f((i - 1) * scale, (j - 1) * scale, 2), rotation, flip);
            }
        }
        
        this.gameObject.addComponent(compSprite);
        return this;
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

    /*
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
