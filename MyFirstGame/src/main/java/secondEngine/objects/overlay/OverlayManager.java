package secondEngine.objects.overlay;

import java.util.Optional;

import secondEngine.components.helpers.Sprite;
import secondEngine.components.helpers.SpriteSheet;
import secondEngine.objects.Tiles.GroundManager.GroundPrefabs;
import secondEngine.util.AssetPool;
import secondEngine.util.PrefabFactory.PrefabIds;
import secondEngine.util.PrefabFactory.PrefabManagerBase;

public class OverlayManager implements PrefabManagerBase {
    public interface OverlayPrefabs extends PrefabIds {
        enum InventoryLayout implements GroundPrefabs {
            DEAFULT_1(1), DEFAULT_27(27), DEFAULT_9(9);

            private int nSlots;

            private InventoryLayout(int nSlots) {
                this.nSlots = nSlots;
            }

            public int getNumSlots() {
                return this.nSlots;
            }

            @Override
            public Categories id() {
                return Categories.OVERLAY;
            }
        }

        enum Special implements OverlayPrefabs {
            GRID, F3_INFO;

            @Override
            public Categories id() {
                return Categories.OVERLAY;
            }
        }
    }

    @Override
    public <OverlayType extends PrefabIds> Optional<SpriteSheet> getSpriteSheet(OverlayType objectId) {
        if (objectId.isOfType(OverlayPrefabs.InventoryLayout.class)) {
            return Optional.of(AssetPool.getSpriteSheet("overlay/inventory_2_2_4.png"));
        }
        System.err.println("Unable to find the specified Overlay element:" + objectId.toString());
        return Optional.empty();
    }

    @Override
    public <OverlayType extends PrefabIds> Optional<Sprite> getSprite(OverlayType objectId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSprite'");
    }
}
