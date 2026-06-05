package req2;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Depositable;
import game.utility.LootExplosion;
import testutil.TestWorld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LootExplosionTest {

    private GameMap testMap;
    private LootExplosion lootExplosion;

    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(5, 5);
        lootExplosion = new LootExplosion();
    }

    @Test
    void explodeDropsItemOnEachAdjacentTile() {
        Location centre = testMap.at(2, 2);
        lootExplosion.explode(centre);

        int adjacentTilesWithItems = 0;
        for (Exit exit : centre.getExits()) {
            if (!exit.getDestination().getItems().isEmpty()) {
                adjacentTilesWithItems++;
            }
        }
        assertEquals(8, adjacentTilesWithItems);
    }

    @Test
    void explodeDropsDepositableItems() {
        Location centre = testMap.at(2, 2);
        lootExplosion.explode(centre);

        for (Exit exit : centre.getExits()) {
            List<Item> items = exit.getDestination().getItems();
            assertFalse(items.isEmpty(), "Each adjacent tile should have an item");
            boolean isDepositable = items.stream()
                    .anyMatch(item -> item.asCapability(Depositable.class).isPresent());
            assertTrue(isDepositable, "Dropped item must implement Depositable");
        }
    }

    @Test
    void explodeDoesNotDropOnCentreTile() {
        Location centre = testMap.at(2, 2);
        lootExplosion.explode(centre);
        assertTrue(centre.getItems().isEmpty(), "Centre tile itself should have no items");
    }

    @Test
    void explodeOnEdgeTileDropsOnlyOnValidExits() {
        Location corner = testMap.at(0, 0);
        int exitCount = corner.getExits().size();
        lootExplosion.explode(corner);

        int tilesWithItems = 0;
        for (Exit exit : corner.getExits()) {
            if (!exit.getDestination().getItems().isEmpty()) {
                tilesWithItems++;
            }
        }
        assertEquals(exitCount, tilesWithItems);
    }
}

