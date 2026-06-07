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

/**
 * Unit tests for {@link LootExplosion}: item distribution on adjacent tiles,
 * depositability of dropped items, centre-tile exclusion, and edge-tile behaviour.
 */
class LootExplosionTest {

    private GameMap testMap;
    private LootExplosion lootExplosion;

    /** Initialises a 5×5 floor map and creates a fresh LootExplosion instance. */
    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(5, 5);
        lootExplosion = new LootExplosion();
    }

    /** Verifies that {@code explode} drops exactly one item on each of the 8 adjacent tiles. */
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

    /** Verifies that every item dropped by the explosion implements {@link Depositable}. */
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

    /** Verifies that the centre tile itself has no items after the explosion. */
    @Test
    void explodeDoesNotDropOnCentreTile() {
        Location centre = testMap.at(2, 2);
        lootExplosion.explode(centre);
        assertTrue(centre.getItems().isEmpty(), "Centre tile itself should have no items");
    }

    /** Verifies that exploding at a corner tile drops items on all available (valid) adjacent exits. */
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
