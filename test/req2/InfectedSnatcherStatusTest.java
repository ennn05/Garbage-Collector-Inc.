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

class InfectedSnatcherStatusTest {

    private GameMap testMap;
    private ScrapSnatcher snatcher;

    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(5, 5);
        snatcher = new ScrapSnatcher();
        testMap.addActor(snatcher, testMap.at(2, 2));
    }

    @Test
    void statusActiveOnCreation() {
        InfectedSnatcherStatus status = new InfectedSnatcherStatus(snatcher);
        assertTrue(status.isStatusActive());
    }

    @Test
    void statusDeals1DamagePerTick() {
        InfectedSnatcherStatus status = new InfectedSnatcherStatus(snatcher);
        int hpBefore = snatcher.getStatistic(ActorStatistics.HEALTH);
        status.tickStatus(snatcher, testMap.at(2, 2));
        assertEquals(hpBefore - 1, snatcher.getStatistic(ActorStatistics.HEALTH));
    }

    @Test
    void statusBecomesInactiveWhenSnatcherDies() {
        InfectedSnatcherStatus status = new InfectedSnatcherStatus(snatcher);
        // Damage snatcher to 1 HP then tick â€” the tick deals 1 damage, killing it
        snatcher.hurt(snatcher.getStatistic(ActorStatistics.HEALTH) - 1);
        status.tickStatus(snatcher, testMap.at(2, 2));
        assertFalse(status.isStatusActive());
    }

    @Test
    void snatcherIsRemovedFromMapOnDeath() {
        InfectedSnatcherStatus status = new InfectedSnatcherStatus(snatcher);
        snatcher.hurt(snatcher.getStatistic(ActorStatistics.HEALTH) - 1);
        status.tickStatus(snatcher, testMap.at(2, 2));
        assertFalse(testMap.contains(snatcher),
                "Dead snatcher must be removed from the map");
    }

    @Test
    void infectedSnatcherHasStatus() {
        snatcher.infect(snatcher, testMap);
        assertTrue(snatcher.hasStatus(InfectedSnatcherStatus.class));
    }

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

    @Test
    void infectedSnatcherDoesNothingWhenNoWorkerAdjacent() throws Exception {
        snatcher.addStatus(new InfectedSnatcherStatus(snatcher));

        Action action = snatcher.playTurn(new ActionList(), new DoNothingAction(), testMap, new Display());

        assertEquals(DoNothingAction.class, action.getClass(),
                "Infected snatcher with no adjacent worker should do nothing");
    }
}

