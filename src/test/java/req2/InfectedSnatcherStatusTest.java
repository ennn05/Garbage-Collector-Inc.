package req2;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.AttackWorkerAction;
import game.actors.ContractedWorker;
import game.actors.ScrapSnatcher;
import game.inventory.BasicInventory;
import game.items.AluminiumScrap;
import game.status.InfectedSnatcherStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link InfectedSnatcherStatus}: activation, per-tick damage,
 * death-triggered deactivation, map removal on death, and behaviour override.
 */
class InfectedSnatcherStatusTest {

    private GameMap testMap;
    private ScrapSnatcher snatcher;

    /** Initialises a 5×5 floor map and places a ScrapSnatcher at (2,2). */
    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(5, 5);
        snatcher = new ScrapSnatcher();
        testMap.addActor(snatcher, testMap.at(2, 2));
    }

    /** Verifies that a newly created InfectedSnatcherStatus is active. */
    @Test
    void statusActiveOnCreation() {
        InfectedSnatcherStatus status = new InfectedSnatcherStatus(snatcher);
        assertTrue(status.isStatusActive());
    }

    /** Verifies that each call to {@code tickStatus} deals exactly 1 damage to the snatcher. */
    @Test
    void statusDeals1DamagePerTick() {
        InfectedSnatcherStatus status = new InfectedSnatcherStatus(snatcher);
        int hpBefore = snatcher.getStatistic(ActorStatistics.HEALTH);
        status.tickStatus(snatcher, testMap.at(2, 2));
        assertEquals(hpBefore - 1, snatcher.getStatistic(ActorStatistics.HEALTH));
    }

    /** Verifies that the status becomes inactive after the tick kills the snatcher. */
    @Test
    void statusBecomesInactiveWhenSnatcherDies() {
        InfectedSnatcherStatus status = new InfectedSnatcherStatus(snatcher);
        // Damage snatcher to 1 HP then tick — the tick deals 1 damage, killing it
        snatcher.hurt(snatcher.getStatistic(ActorStatistics.HEALTH) - 1);
        status.tickStatus(snatcher, testMap.at(2, 2));
        assertFalse(status.isStatusActive());
    }

    /** Verifies that a snatcher killed by the status tick is removed from the map. */
    @Test
    void snatcherIsRemovedFromMapOnDeath() {
        InfectedSnatcherStatus status = new InfectedSnatcherStatus(snatcher);
        snatcher.hurt(snatcher.getStatistic(ActorStatistics.HEALTH) - 1);
        status.tickStatus(snatcher, testMap.at(2, 2));
        assertFalse(testMap.contains(snatcher),
                "Dead snatcher must be removed from the map");
    }

    /** Verifies that calling {@code infect} adds an InfectedSnatcherStatus to the snatcher. */
    @Test
    void infectedSnatcherHasStatus() {
        snatcher.infect(snatcher, testMap);
        assertTrue(snatcher.hasStatus(InfectedSnatcherStatus.class));
    }

    /** Verifies that an infected snatcher chooses AttackWorkerAction over snatching when a worker is adjacent. */
    @Test
    void infectedSnatcherChoosesAttackOverSnatching() throws Exception {
        ContractedWorker worker = new ContractedWorker("Worker", 'W', 10, new BasicInventory());
        testMap.addActor(worker, testMap.at(3, 2));
        testMap.at(2, 1).addItem(new AluminiumScrap());
        snatcher.addStatus(new InfectedSnatcherStatus(snatcher));

        Action action = snatcher.playTurn(new ActionList(), new DoNothingAction(), testMap, new Display());

        assertEquals(AttackWorkerAction.class, action.getClass(),
                "Infected snatcher must attack worker rather than snatch nearby resource");
    }

    /** Verifies that an infected snatcher with no adjacent worker performs DoNothingAction. */
    @Test
    void infectedSnatcherDoesNothingWhenNoWorkerAdjacent() throws Exception {
        snatcher.addStatus(new InfectedSnatcherStatus(snatcher));

        Action action = snatcher.playTurn(new ActionList(), new DoNothingAction(), testMap, new Display());

        assertEquals(DoNothingAction.class, action.getClass(),
                "Infected snatcher with no adjacent worker should do nothing");
    }
}
