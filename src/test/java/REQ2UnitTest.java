import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.actions.AttackWorkerAction;
import game.actions.InfectAction;
import game.actions.MoveSnatchResourceAction;
import game.actors.ContractedWorker;
import game.actors.Parasite;
import game.actors.ScrapSnatcher;
import game.behaviours.SnatchDepositableResourceBehaviour;
import game.enums.Ability;
import game.flora.DeprecatedFleshyMatureTree;
import game.flora.DeprecatedFleshySprout;
import game.flora.FleshyMonolith;
import game.grounds.Floor;
import game.grounds.Wall;
import game.interfaces.Depositable;
import game.interfaces.Infectable;
import game.interfaces.Spawnable;
import game.inventory.BasicInventory;
import game.items.AluminiumScrap;
import game.status.InfectedSnatcherStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for Assignment 3 REQ2: The Scrap Snatcher.
 *
 * These tests verify deterministic and isolated behaviours introduced for REQ2:
 * Scrap Snatcher creation, loot explosion, resource snatching, infection,
 * infected behaviour, and 99-Deprecated fleshy tree behaviour.
 */
public class REQ2UnitTest {

    /**
     * Minimal world fixture so GameMap actor movement works through the engine.
     */
    private static class TestWorld extends World {
        TestWorld() {
            super(new Display());
        }
    }

    /**
     * A simple non-depositable item used as an invalid test value.
     */
    private static class NonDepositableTestItem extends Item {
        NonDepositableTestItem() {
            super("Non-depositable Test Item", 'n');
            this.makePortable();
        }
    }

    /**
     * Creates a test map and attaches it to a small test world.
     *
     * @param name map name
     * @param lines ASCII map layout
     * @return a GameMap ready for testing
     * @throws GameEngineException if the map cannot be created
     */
    private GameMap createTestMap(String name, List<String> lines) throws GameEngineException {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Floor::new);
        groundCreator.registerGround('#', Wall::new);

        GameMap map = new GameMap(name, groundCreator, lines);
        TestWorld testWorld = new TestWorld();
        testWorld.addGameMap(map);

        return map;
    }

    /**
     * Creates a 3x3 open map.
     *
     * @return an open 3x3 map
     * @throws GameEngineException if the map cannot be created
     */
    private GameMap createOpenThreeByThreeMap() throws GameEngineException {
        return createTestMap("test-map", Arrays.asList(
                "...",
                "...",
                "..."
        ));
    }

    /**
     * Creates a 5x5 open map.
     *
     * @return an open 5x5 map
     * @throws GameEngineException if the map cannot be created
     */
    private GameMap createOpenFiveByFiveMap() throws GameEngineException {
        return createTestMap("test-map", Arrays.asList(
                ".....",
                ".....",
                ".....",
                ".....",
                "....."
        ));
    }

    /**
     * Creates a worker test actor.
     *
     * @return a worker actor
     */
    private ContractedWorker createWorker() {
        return new ContractedWorker("Test Worker", 'W', 10, new BasicInventory());
    }

    /** Verifies that a freshly created ScrapSnatcher has the expected display char, HP, abilities, and capabilities. */
    @Test
    public void typicalScrapSnatcherHasCorrectStatsAndCapabilities() {
        ScrapSnatcher snatcher = new ScrapSnatcher();

        Assertions.assertEquals('s', snatcher.getDisplayChar());
        Assertions.assertEquals(25, snatcher.getStatistic(ActorStatistics.HEALTH));
        Assertions.assertTrue(snatcher.hasAbility(Ability.HOSTILE));
        Assertions.assertTrue(snatcher.asCapability(Spawnable.class).isPresent());
        Assertions.assertTrue(snatcher.asCapability(Infectable.class).isPresent());
    }

    /** Verifies that spawning a ScrapSnatcher drops one depositable resource on each adjacent tile. */
    @Test
    public void typicalLootExplosionDropsOneDepositableResourceOnEachAdjacentTile()
            throws GameEngineException {
        GameMap map = createOpenThreeByThreeMap();
        Location spawnLocation = map.at(1, 1);
        ScrapSnatcher snatcher = new ScrapSnatcher();

        snatcher.spawnEffect(spawnLocation);

        for (Exit exit : spawnLocation.getExits()) {
            Location adjacent = exit.getDestination();

            Assertions.assertEquals(1, adjacent.getItems().size());

            Item droppedItem = adjacent.getItems().get(0);
            Assertions.assertTrue(droppedItem.asCapability(Depositable.class).isPresent());
        }
    }

    /** Verifies that a loot explosion at a map corner only drops items on the 3 existing adjacent tiles. */
    @Test
    public void edgeLootExplosionAtMapCornerOnlyAffectsExistingAdjacentTiles()
            throws GameEngineException {
        GameMap map = createOpenThreeByThreeMap();
        Location cornerLocation = map.at(0, 0);
        ScrapSnatcher snatcher = new ScrapSnatcher();

        snatcher.spawnEffect(cornerLocation);

        Assertions.assertEquals(3, cornerLocation.getExits().size());

        for (Exit exit : cornerLocation.getExits()) {
            Location adjacent = exit.getDestination();

            Assertions.assertEquals(1, adjacent.getItems().size());

            Item droppedItem = adjacent.getItems().get(0);
            Assertions.assertTrue(droppedItem.asCapability(Depositable.class).isPresent());
        }
    }

    /** Verifies that the snatch behaviour moves the snatcher to the resource tile and picks up the depositable item. */
    @Test
    public void typicalSnatchBehaviourMovesToResourceTileAndPicksUpDepositableResource()
            throws GameEngineException {
        GameMap map = createTestMap("test-map", Arrays.asList(
                "###",
                "#..",
                "###"
        ));

        ScrapSnatcher snatcher = new ScrapSnatcher();
        AluminiumScrap scrap = new AluminiumScrap();

        Location start = map.at(1, 1);
        Location resourceTile = map.at(2, 1);

        map.addActor(snatcher, start);
        resourceTile.addItem(scrap);

        SnatchDepositableResourceBehaviour behaviour = new SnatchDepositableResourceBehaviour();
        Action action = behaviour.operate(snatcher, start);

        Assertions.assertEquals(MoveSnatchResourceAction.class, action.getClass());

        action.execute(snatcher, map);

        Assertions.assertSame(resourceTile, map.locationOf(snatcher));
        Assertions.assertFalse(resourceTile.getItems().contains(scrap));
        Assertions.assertTrue(snatcher.getInventory().getItems().contains(scrap));
    }

    /** Verifies that MoveSnatchResourceAction does not pick up a non-depositable item, leaving it on the tile. */
    @Test
    public void invalidMoveSnatchActionDoesNotPickUpNonDepositableItem()
            throws GameEngineException {
        GameMap map = createTestMap("test-map", Arrays.asList(
                "###",
                "#..",
                "###"
        ));

        ScrapSnatcher snatcher = new ScrapSnatcher();
        Item nonDepositableItem = new NonDepositableTestItem();

        Location start = map.at(1, 1);
        Location targetTile = map.at(2, 1);

        map.addActor(snatcher, start);
        targetTile.addItem(nonDepositableItem);

        Action action = new MoveSnatchResourceAction(targetTile, "East", nonDepositableItem);
        String result = action.execute(snatcher, map);

        Assertions.assertSame(targetTile, map.locationOf(snatcher));
        Assertions.assertTrue(targetTile.getItems().contains(nonDepositableItem));
        Assertions.assertFalse(snatcher.getInventory().getItems().contains(nonDepositableItem));
        Assertions.assertTrue(result.contains("finds no depositable resource"));
    }

    /** Verifies that the snatch behaviour returns null when the snatcher has no valid exits. */
    @Test
    public void edgeSnatchBehaviourReturnsNullWhenNoValidMovementExists()
            throws GameEngineException {
        GameMap map = createTestMap("test-map", Arrays.asList(
                "###",
                "#.#",
                "###"
        ));

        ScrapSnatcher snatcher = new ScrapSnatcher();
        Location trappedLocation = map.at(1, 1);

        map.addActor(snatcher, trappedLocation);

        SnatchDepositableResourceBehaviour behaviour = new SnatchDepositableResourceBehaviour();
        Action action = behaviour.operate(snatcher, trappedLocation);

        Assertions.assertNull(action);
    }

    /** Verifies that InfectAction removes the Parasite from the map and adds InfectedSnatcherStatus to the target. */
    @Test
    public void typicalParasiteInfectActionRemovesParasiteAndAddsInfectedStatus()
            throws GameEngineException {
        GameMap map = createOpenThreeByThreeMap();

        Parasite parasite = new Parasite();
        ScrapSnatcher snatcher = new ScrapSnatcher();

        map.addActor(parasite, map.at(1, 1));
        map.addActor(snatcher, map.at(2, 1));

        Assertions.assertTrue(snatcher.asCapability(Infectable.class).isPresent());
        Infectable target = snatcher.asCapability(Infectable.class).get();

        Action infectAction = new InfectAction(target, "East");
        String result = infectAction.execute(parasite, map);

        Assertions.assertFalse(map.contains(parasite));
        Assertions.assertTrue(snatcher.hasStatus(InfectedSnatcherStatus.class));
        Assertions.assertTrue(result.contains("infects"));
    }

    /** Verifies that an infected snatcher takes exactly 1 damage when the InfectedSnatcherStatus ticks. */
    @Test
    public void typicalInfectedSnatcherTakesOneDamageWhenStatusTicks()
            throws GameEngineException {
        GameMap map = createOpenThreeByThreeMap();

        ScrapSnatcher snatcher = new ScrapSnatcher();
        Location location = map.at(1, 1);

        map.addActor(snatcher, location);
        snatcher.addStatus(new InfectedSnatcherStatus(snatcher));

        int healthBeforeTick = snatcher.getStatistic(ActorStatistics.HEALTH);

        snatcher.tickStatuses(location);

        Assertions.assertEquals(
                healthBeforeTick - 1,
                snatcher.getStatistic(ActorStatistics.HEALTH)
        );
    }

    /** Verifies that an infected snatcher selects AttackWorkerAction over snatching when a worker is adjacent. */
    @Test
    public void typicalInfectedSnatcherChoosesAttackWorkerActionInsteadOfSnatching()
            throws GameEngineException {
        GameMap map = createOpenThreeByThreeMap();

        ScrapSnatcher snatcher = new ScrapSnatcher();
        ContractedWorker worker = createWorker();

        map.addActor(snatcher, map.at(1, 1));
        map.addActor(worker, map.at(2, 1));
        map.at(1, 0).addItem(new AluminiumScrap());

        snatcher.addStatus(new InfectedSnatcherStatus(snatcher));

        Action action = snatcher.playTurn(
                new ActionList(),
                new DoNothingAction(),
                map,
                new Display()
        );

        Assertions.assertEquals(AttackWorkerAction.class, action.getClass());
    }

    /** Verifies that an infected snatcher performs DoNothingAction when no worker is adjacent. */
    @Test
    public void edgeInfectedSnatcherDoesNothingWhenNoWorkerIsAdjacent()
            throws GameEngineException {
        GameMap map = createOpenThreeByThreeMap();

        ScrapSnatcher snatcher = new ScrapSnatcher();

        map.addActor(snatcher, map.at(1, 1));
        snatcher.addStatus(new InfectedSnatcherStatus(snatcher));

        Action action = snatcher.playTurn(
                new ActionList(),
                new DoNothingAction(),
                map,
                new Display()
        );

        Assertions.assertEquals(DoNothingAction.class, action.getClass());
    }

    /** Verifies that DeprecatedFleshySprout spawns an Undead on an adjacent tile when a worker is nearby. */
    @Test
    public void typicalDeprecatedFleshySproutSpawnsUndeadWhenWorkerIsNearby()
            throws GameEngineException {
        GameMap map = createOpenThreeByThreeMap();

        Location treeLocation = map.at(1, 1);
        treeLocation.setGround(new DeprecatedFleshySprout());

        ContractedWorker worker = createWorker();
        map.addActor(worker, map.at(0, 1));

        treeLocation.getGround().tick(treeLocation);

        Location expectedSpawnLocation = map.at(1, 0);

        Assertions.assertTrue(expectedSpawnLocation.containsAnActor());
        Assertions.assertEquals('Ѫ', expectedSpawnLocation.getActor().getDisplayChar());
    }

    /** Verifies that DeprecatedFleshyMatureTree spawns a ScrapSnatcher on an adjacent tile when a worker is nearby. */
    @Test
    public void typicalDeprecatedFleshyMatureTreeSpawnsScrapSnatcherWhenWorkerIsNearby()
            throws GameEngineException {
        GameMap map = createOpenThreeByThreeMap();

        Location treeLocation = map.at(1, 1);
        treeLocation.setGround(new DeprecatedFleshyMatureTree());

        ContractedWorker worker = createWorker();
        map.addActor(worker, map.at(0, 1));

        treeLocation.getGround().tick(treeLocation);

        Location expectedSpawnLocation = map.at(1, 0);

        Assertions.assertTrue(expectedSpawnLocation.containsAnActor());
        Assertions.assertEquals('s', expectedSpawnLocation.getActor().getDisplayChar());
    }

    /** Verifies that DeprecatedFleshySprout does not spawn anything when no worker is nearby. */
    @Test
    public void edgeDeprecatedFleshySproutDoesNotSpawnWhenNoWorkerIsNearby()
            throws GameEngineException {
        GameMap map = createOpenThreeByThreeMap();

        Location treeLocation = map.at(1, 1);
        treeLocation.setGround(new DeprecatedFleshySprout());

        treeLocation.getGround().tick(treeLocation);

        for (Exit exit : treeLocation.getExits()) {
            Assertions.assertFalse(exit.getDestination().containsAnActor());
        }
    }

    /** Verifies that FleshyMonolith warps an adjacent worker to a different location when it ticks. */
    @Test
    public void typicalFleshyMonolithWarpsAdjacentWorkerToDifferentLocation()
            throws GameEngineException {
        GameMap map = createOpenFiveByFiveMap();

        Location monolithLocation = map.at(2, 2);
        monolithLocation.setGround(new FleshyMonolith());

        ContractedWorker worker = createWorker();
        Location originalWorkerLocation = map.at(1, 2);

        map.addActor(worker, originalWorkerLocation);

        monolithLocation.getGround().tick(monolithLocation);

        Assertions.assertNotSame(originalWorkerLocation, map.locationOf(worker));
    }

    /** Verifies that FleshyMonolith does nothing when no worker is adjacent. */
    @Test
    public void edgeFleshyMonolithDoesNothingWhenNoWorkerIsAdjacent()
            throws GameEngineException {
        GameMap map = createOpenThreeByThreeMap();

        Location monolithLocation = map.at(1, 1);
        monolithLocation.setGround(new FleshyMonolith());

        monolithLocation.getGround().tick(monolithLocation);

        for (Exit exit : monolithLocation.getExits()) {
            Assertions.assertFalse(exit.getDestination().containsAnActor());
        }
    }
}