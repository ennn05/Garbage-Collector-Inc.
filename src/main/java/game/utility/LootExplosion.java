package game.utility;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.items.AlienArtifact;
import game.items.AluminiumScrap;
import game.items.IndustrialFan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Handles the loot explosion triggered when a Scrap Snatcher is spawned.
 * <p>
 * One random depositable resource is placed on every valid adjacent tile around
 * the spawn location. This class only manages the resource-dropping effect and
 * does not decide when a Scrap Snatcher should be spawned.
 */
public class LootExplosion {
    private final Random random = new Random();
    private final List<Supplier<Item>> resourceFactories = new ArrayList<>();

    /**
     * Creates a loot explosion effect with the current depositable resource pool.
     */
    public LootExplosion() {
        resourceFactories.add(AluminiumScrap::new);
        resourceFactories.add(IndustrialFan::new);
        resourceFactories.add(AlienArtifact::new);
    }

    /**
     * Places exactly one random depositable resource on each adjacent tile.
     *
     * @param spawnLocation the location where the Scrap Snatcher was spawned
     */
    public void explode(Location spawnLocation) {
        for (Exit exit : spawnLocation.getExits()) {
            exit.getDestination().addItem(createRandomResource());
        }
    }

    /**
     * Creates one random resource from the available depositable resource pool.
     *
     * @return a newly created depositable resource item
     */
    private Item createRandomResource() {
        return resourceFactories.get(random.nextInt(resourceFactories.size())).get();
    }
}