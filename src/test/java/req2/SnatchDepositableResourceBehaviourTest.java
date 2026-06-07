package req2;

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

class SnatchDepositableResourceBehaviourTest {

    private GameMap testMap;
    private ScrapSnatcher snatcher;
    private SnatchDepositableResourceBehaviour behaviour;

    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(5, 5);
        snatcher = new ScrapSnatcher();
        testMap.addActor(snatcher, testMap.at(2, 2));
        behaviour = new SnatchDepositableResourceBehaviour();
    }

    @Test
    void returnsActionWhenDepositableItemOnAdjacentTile() {
        testMap.at(3, 2).addItem(new AluminiumScrap());
        Action action = behaviour.operate(snatcher, testMap.at(2, 2));
        assertNotNull(action, "Should return a snatch action when depositable item is adjacent");
    }

    @Test
    void returnsActionEvenWithNoDepositableItem() {
        // Random wander action returned when no depositable resource found
        Action action = behaviour.operate(snatcher, testMap.at(2, 2));
        assertNotNull(action, "Should still return a move action when no depositable item nearby");
    }

    @Test
    void doesNotSnatchNonDepositableItem() {
        // Flask is not Depositable â€” snatcher should just wander, not snatch
        testMap.at(3, 2).addItem(new Flask());
        behaviour.operate(snatcher, testMap.at(2, 2));
        assertFalse(testMap.at(3, 2).getItems().isEmpty(),
                "Non-depositable items should not be snatched");
    }

    @Test
    void snatchedItemEndsUpInSnatcherInventoryViaMoveSnatchAction() throws Exception {
        AluminiumScrap scrap = new AluminiumScrap();
        testMap.at(3, 2).addItem(scrap);

        MoveSnatchResourceAction action = new MoveSnatchResourceAction(testMap.at(3, 2), "East", scrap);
        action.execute(snatcher, testMap);

        assertTrue(snatcher.getInventory().getItems().contains(scrap),
                "Snatched item should be in snatcher inventory after MoveSnatchResourceAction executes");
    }

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

