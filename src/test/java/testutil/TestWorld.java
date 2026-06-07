package testutil;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import game.grounds.Floor;
import game.grounds.Wall;

/**
 * Minimal World subclass used only in tests to properly initialise GameMap.actorLocations.
 * GameMap.actorLocations is only set via World.addGameMap(), so every test that calls
 * map.addActor() must register the map through this class first.
 */
public class TestWorld extends World {

    /** Creates a TestWorld backed by a no-op Display. */
    public TestWorld() {
        super(new Display());
    }

    /**
     * Creates a {@code width × height} map of Floor tiles registered with this world.
     *
     * @param width  number of columns
     * @param height number of rows
     * @return a fully registered {@link GameMap} ready for actor placement
     * @throws Exception if the map cannot be created
     */
    public GameMap createFloorMap(int width, int height) throws Exception {
        DefaultGroundCreator creator = new DefaultGroundCreator();
        creator.registerGround('_', Floor::new);
        creator.registerGround('#', Wall::new);
        GameMap map = new GameMap("test", creator, '_', width, height);
        addGameMap(map);
        return map;
    }
}
