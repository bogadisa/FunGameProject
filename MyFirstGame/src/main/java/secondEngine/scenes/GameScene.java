package secondEngine.scenes;

import java.util.Map;

import org.joml.Vector2f;
import org.joml.Vector3f;

import secondEngine.Camera;
import secondEngine.Config;
import secondEngine.Config.UIconfig;
import secondEngine.components.CompositeSpriteRenderer;
import secondEngine.components.Inventory;
import secondEngine.components.Overlay;
import secondEngine.components.SpriteRenderer;
import secondEngine.components.Transform;
import secondEngine.components.helpers.InventorySlot;
import secondEngine.components.helpers.Sprite;
import secondEngine.components.helpers.SpriteSheet;
import secondEngine.components.AnimationStateMachine;
import secondEngine.grid.SpatialGrid;
import secondEngine.objects.GameObject;
import secondEngine.objects.Special.Mouse;
import secondEngine.objects.Tiles.GroundManager.GroundPrefabs;
import secondEngine.objects.overlay.Layout;
import secondEngine.util.AssetPool;
import secondEngine.util.PrefabFactory;
import secondEngine.objects.overlay.OverlayManager.OverlayPrefabs;
import secondEngine.objects.overlay.OverlayManager.OverlayPrefabs.InventoryLayout;

public class GameScene extends Scene {
    public void init() {
        Config.getUIconfig();
        int scale = UIconfig.getScale();
        loadResources();

        this.camera = new Camera(new Vector2f());
        this.worldGrid = new SpatialGrid("world");
        this.screenGrid = new SpatialGrid("screen");

        GameObject grid = PrefabFactory.getObject(OverlayPrefabs.Special.GRID);
        this.addGameObjectToScene(grid);

        GameObject mouse = Mouse.generate();
        this.addGameObjectToScene(mouse);

        // GameObject centre = PrefabFactory.getObject(GroundPrefabs.Spring.DIRT_BONE);
        // centre.transform.position.x = 256;
        // centre.transform.position.y = 256;
        // centre.transform.position.z = 11;
        // this.addGameObjectToScene(centre);

        GameObject someOverlay = this.createGameObject("someOverlay", new Transform().init(new Vector3f(256, 256, 0)));

        SpriteSheet sprites = AssetPool.getSpriteSheet("overlay/inventory_2_2_4.png");
        Sprite cornerSprite = sprites.getSprite(0);
        Sprite edgeSprite = sprites.getSprite(1);
        Sprite inventorySprite = sprites.getSprite(2);
        Sprite fillSprite = sprites.getSprite(3);
        Sprite[] overlaySprites = { fillSprite, cornerSprite, edgeSprite };
        Overlay overlay = someOverlay.addComponent(new Overlay()).init(11, 6, 0.25f, overlaySprites, false);
        Map<Layout.SlotType, Sprite> layoutSprites = Map.of(Layout.SlotType.Interactable.INVENTORY, inventorySprite);
        InventoryLayout layout = InventoryLayout.TEST_36;
        overlay.addLayout(AssetPool.getLayout(layout), layoutSprites);
        Inventory inventory = someOverlay.addComponent(new Inventory()).init(layout.getNumSlots(), 64, true);
        Inventory inventoryObj = new Inventory().init(2, 64);
        InventorySlot slot1 = new InventorySlot(inventoryObj, GroundPrefabs.Spring.DIRT_1, 1, 64);
        inventory.transferFrom(slot1, 1);
        this.addGameObjectToScene(someOverlay);
        worldGrid.addObject(someOverlay);
        // GameObject infoObject =
        // PrefabFactory.getObject(OverlayPrefabs.Special.F3_INFO);
        // this.addGameObjectToScene(infoObject);
        // if (Window.getScene().isLoaded()) {
        // return;
        // }

        // try {
        // Font font = new Font().init("resources/fonts/Unifontexmono-lxY45.ttf");
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // GameObject textObj = this.createGameObject("text", new Transform().init(new
        // Vector3f(1000, 400, 1)));

        // TextRenderer renderer = new TextRenderer();
        // TextBox textBox = new TextBox(100, 20, new Vector3f(200, 200, 0));
        // renderer.addTextBox(textBox);
        // textBox.addText("I love Anna");
        // TextBox textBox2 = new TextBox(100, 20, new Vector3f(200, 100, 0));
        // renderer.addTextBox(textBox2);
        // textBox2.addText("I love Anna");
        // textObj.addComponent(renderer);
        // this.addGameObjectToScene(textObj);

        // GameObject player = Player.generate();
        // player.transform.position.add(64, 80, 0);
        // this.addGameObjectToScene(player);

        // TestManager.testComponent(Inventory.class);

        // GameObject obj = new GameObject("Obj");
        // CompositeSpriteRenderer compSprite = new CompositeSpriteRenderer();
        // compSprite.init()
        // .addSpriteRenderer(new SpriteRenderer().setSprite(new Sprite()))
        // .addSpriteRenderer(new SpriteRenderer().setSprite(new Sprite()), new
        // Vector3f(64, 0, -2), 60);
        // obj.addComponent(compSprite);
        // obj.addComponent(new Transform().init(new Vector3f(256, 100, 0), new
        // Vector3f(2*scale, 2*scale, 1)));
        // this.addGameObjectToScene(obj);

        // GameObject inventory =
        // PrefabFactory.getObject(OverlayPrefabs.InventoryLayout.DEFAULT_27);
        // this.addGameObjectToScene(inventory);

        // Window.getScene().worldGrid().addObject(inventory);

        // ui.getComponent(GridState.class).toggleHighlight();

    }

    private void loadResources() {
        // TODO move this to a seperate initializer class
        AssetPool.getShader("/shaders/sprites/default.glsl");
        AssetPool.getShader("/shaders/fonts/default.glsl");

        AssetPool.addFont("Unifontexmono-lxY45.ttf");
        // AssetPool.addFont("HerculesPixelFontRegular-ovAX0.otf");

        AssetPool.addSpriteSheet("entities/player_3_3_9.png");
        AssetPool.addSpriteSheet("overlay/overlaySmallBasics_2_2_4.png");
        AssetPool.addSpriteSheet("overlay/inventory_2_2_4.png");
        AssetPool.addSpriteSheet("background/spring_4_4_15.png");

        for (GameObject go : this.gameObjects) {
            if (go.getComponent(CompositeSpriteRenderer.class) != null) {
                go.getComponent(CompositeSpriteRenderer.class).ifPresent(compSpr -> compSpr.refreshTextures());
                ;
            } else if (go.getComponent(SpriteRenderer.class) != null) {
                go.getComponent(SpriteRenderer.class).ifPresent(spr -> spr.refreshTexture());
            }

            if (go.getComponent(AnimationStateMachine.class) != null) {
                go.getComponent(AnimationStateMachine.class).ifPresent(sm -> sm.refreshTextures());
            }
        }

        Layout.SlotType.Other n = Layout.SlotType.Other.NULL;
        Layout.SlotType.Interactable i = Layout.SlotType.Interactable.INVENTORY;
        Layout.SlotType[][] standardLayout = { new Layout.SlotType[11], { n, i, i, i, i, i, i, i, i, i, n },
                { n, i, i, i, i, i, i, i, i, i, n }, { n, i, i, i, i, i, i, i, i, i, n }, new Layout.SlotType[11] };
        Layout standard = new Layout(standardLayout);
        AssetPool.addLayout(standard, InventoryLayout.DEFAULT_27);
        Layout.SlotType[][] testLayout = { new Layout.SlotType[11], { n, i, i, i, i, i, i, i, i, i, n },
                { n, i, i, i, i, i, i, i, i, i, n }, { n, i, i, i, i, i, i, i, i, i, n },
                { n, i, i, i, i, i, i, i, i, i, n }, new Layout.SlotType[11] };
        Layout test = new Layout(testLayout);
        AssetPool.addLayout(test, InventoryLayout.TEST_36);
        Layout.SlotType[][] lineLayout = { new Layout.SlotType[11], { n, i, i, i, i, i, i, i, i, i, n },
                new Layout.SlotType[11] };
        Layout line = new Layout(lineLayout);
        AssetPool.addLayout(line, InventoryLayout.DEFAULT_9);
    }
}
