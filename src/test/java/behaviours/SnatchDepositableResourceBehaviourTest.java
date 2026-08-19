package behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.MoveSnatchResourceAction;
import game.actors.ScrapSnatcher;
import game.behaviours.SnatchDepositableResourceBehaviour;
import game.grounds.Wall;
import game.items.AluminiumScrap;
import game.items.Flask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SnatchDepositableResourceBehaviour} and {@link MoveSnatchResourceAction}:
 * action selection, non-depositable item avoidance, successful snatch, trapped-actor edge cases.
 */
class SnatchDepositableResourceBehaviourTest {

    private GameMap testMap;
    private ScrapSnatcher snatcher;
    private SnatchDepositableResourceBehaviour behaviour;

    /** Initialises a 5×5 floor map, places the snatcher at (2,2), and creates the behaviour. */
    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(5, 5);
        snatcher = new ScrapSnatcher();
        testMap.addActor(snatcher, testMap.at(2, 2));
        behaviour = new SnatchDepositableResourceBehaviour();
    }

    /** Verifies that the behaviour returns a non-null action when a depositable item is adjacent. */
    @Test
    void returnsActionWhenDepositableItemOnAdjacentTile() {
        testMap.at(3, 2).addItem(new AluminiumScrap());
        Action action = behaviour.operate(snatcher, testMap.at(2, 2));
        assertNotNull(action, "Should return a snatch action when depositable item is adjacent");
    }

    /** Verifies that the behaviour still returns a (wander) action when no depositable resource is found. */
    @Test
    void returnsActionEvenWithNoDepositableItem() {
        // Random wander action returned when no depositable resource found
        Action action = behaviour.operate(snatcher, testMap.at(2, 2));
        assertNotNull(action, "Should still return a move action when no depositable item nearby");
    }

    /** Verifies that a non-depositable item (e.g., Flask) is not snatched and remains on the tile. */
    @Test
    void doesNotSnatchNonDepositableItem() {
        // Flask is not Depositable — snatcher should just wander, not snatch
        testMap.at(3, 2).addItem(new Flask());
        behaviour.operate(snatcher, testMap.at(2, 2));
        assertFalse(testMap.at(3, 2).getItems().isEmpty(),
                "Non-depositable items should not be snatched");
    }

    /** Verifies that executing MoveSnatchResourceAction places the snatched item in the snatcher's inventory. */
    @Test
    void snatchedItemEndsUpInSnatcherInventoryViaMoveSnatchAction() throws Exception {
        AluminiumScrap scrap = new AluminiumScrap();
        testMap.at(3, 2).addItem(scrap);

        MoveSnatchResourceAction action = new MoveSnatchResourceAction(testMap.at(3, 2), "East", scrap);
        action.execute(snatcher, testMap);

        assertTrue(snatcher.getInventory().getItems().contains(scrap),
                "Snatched item should be in snatcher inventory after MoveSnatchResourceAction executes");
    }

    /** Verifies that the behaviour returns {@code null} when the snatcher has no valid exits (surrounded by walls). */
    @Test
    void behaviourReturnsNullWhenSnatcherIsTrapped() throws Exception {
        GameMap map = new TestWorld().createFloorMap(3, 3);
        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 3; y++)
                if (x != 1 || y != 1) map.at(x, y).setGround(new Wall());

        ScrapSnatcher trapped = new ScrapSnatcher();
        map.addActor(trapped, map.at(1, 1));

        Action action = new SnatchDepositableResourceBehaviour().operate(trapped, map.at(1, 1));
        assertNull(action, "Snatcher with no valid exits should return null");
    }

    /** Verifies that MoveSnatchResourceAction does not pick up a non-depositable item and leaves it on the tile. */
    @Test
    void moveSnatchActionSkipsNonDepositableItem() throws Exception {
        Item nonDepositable = new NonDepositableItem();
        testMap.at(3, 2).addItem(nonDepositable);

        MoveSnatchResourceAction action = new MoveSnatchResourceAction(testMap.at(3, 2), "East", nonDepositable);
        String result = action.execute(snatcher, testMap);

        assertTrue(testMap.at(3, 2).getItems().contains(nonDepositable),
                "Non-depositable item must remain on tile");
        assertFalse(snatcher.getInventory().getItems().contains(nonDepositable),
                "Non-depositable item must not enter snatcher inventory");
        assertTrue(result.contains("finds no depositable resource"));
    }

    private static class NonDepositableItem extends Item {
        NonDepositableItem() {
            super("Non-depositable Item", 'n');
            this.makePortable();
        }
    }
}
