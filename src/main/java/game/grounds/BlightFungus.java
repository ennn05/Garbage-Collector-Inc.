package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.DropAction;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Seedable;
import game.status.SporeInfection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A creeping mould that infects actors and slowly colonises adjacent floor tiles.
 *
 * When an actor steps on it:
 *   - Applies SporeInfection (actor spreads BlightFungus while infected).
 *   - 25% chance to force-drop one random inventory item.
 *
 * Every 10 turns: converts one randomly chosen adjacent Floor tile into
 * another BlightFungus tile.
 */
public class BlightFungus extends FungalGround {

    private static final int INFECTION_DURATION = 5;
    private static final int DROP_CHANCE = 25;
    private final Random random = new Random();

    /**
     * Creates a BlightFungus tile (display character: {@code β}).
     */
    public BlightFungus() {
        super('β', "BlightFungus");
    }

    /**
     * Infects the actor with {@link SporeInfection} if not already infected,
     * then applies a {@value #DROP_CHANCE}% chance to force-drop one random inventory item.
     *
     * @param actor    the actor standing on this tile
     * @param location the location of this tile
     */
    @Override
    protected void onActorPresent(Actor actor, Location location) {
        if (!actor.hasStatus(SporeInfection.class)) {
            actor.addStatus(new SporeInfection(INFECTION_DURATION));
            System.out.println(actor + " is infected with blight spores!");
        }

        List<Item> items = new ArrayList<>(actor.getInventory().getItems());
        if (!items.isEmpty() && random.nextInt(100) < DROP_CHANCE) {
            Item dropped = items.get(random.nextInt(items.size()));
            new DropAction(dropped).execute(actor, location.map());
            System.out.println(actor + " drops " + dropped + " as fungal growth consumes their equipment!");
        }
    }

    /**
     * Converts one randomly chosen adjacent {@link Seedable} tile into a new BlightFungus.
     * Called every {@code SPREAD_INTERVAL} turns by the parent {@link FungalGround#tick}.
     *
     * @param location the location of this tile
     */
    @Override
    protected void spread(Location location) {
        List<Location> adjacentFloors = new ArrayList<>();
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();
            if (adj.getGroundAs(Seedable.class) != null) {
                adjacentFloors.add(adj);
            }
        }
        if (!adjacentFloors.isEmpty()) {
            adjacentFloors.get(random.nextInt(adjacentFloors.size())).setGround(new BlightFungus());
            System.out.println("BlightFungus spreads to a new tile!");
        }
    }
}
