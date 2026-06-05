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

    public TestWorld() {
        super(new Display());
    }

    /**
     * Creates a width x height map of Floor tiles registered with this world.
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
