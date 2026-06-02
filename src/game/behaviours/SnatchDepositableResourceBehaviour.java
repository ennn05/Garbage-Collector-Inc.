package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.MoveSnatchResourceAction;
import game.interfaces.Depositable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Makes a Scrap Snatcher move randomly and snatch a depositable resource if the
 * randomly selected destination contains one.
 */
public class SnatchDepositableResourceBehaviour implements Behaviour<Actor, Action> {
    private final Random random = new Random();

    /**
     * Randomly chooses a reachable adjacent tile. If that tile contains a
     * depositable resource, the actor moves there and snatches it; otherwise, it
     * simply moves randomly.
     *
     * @param actor the actor performing the behaviour
     * @param location the actor's current location
     * @return an action to move and possibly snatch a resource, or null if no movement is possible
     */
    @Override
    public Action operate(Actor actor, Location location) {
        List<Exit> validExits = new ArrayList<>();

        for (Exit exit : location.getExits()) {
            if (exit.getDestination().canActorEnter(actor)) {
                validExits.add(exit);
            }
        }

        if (validExits.isEmpty()) {
            return null;
        }

        Exit selectedExit = validExits.get(random.nextInt(validExits.size()));
        Location destination = selectedExit.getDestination();
        Item resource = findDepositableResource(destination);

        if (resource != null) {
            return new MoveSnatchResourceAction(destination, selectedExit.getName(), resource);
        }

        return destination.getMoveAction(actor, selectedExit.getName(), selectedExit.getHotKey());
    }

    /**
     * Finds the first depositable resource at a location without depending on a
     * concrete item class.
     *
     * @param location the location to check
     * @return the first depositable item found, or null if none exists
     */
    private Item findDepositableResource(Location location) {
        for (Item item : location.getItems()) {
            Depositable depositable = item.asCapability(Depositable.class).orElse(null);
            if (depositable != null) {
                return item;
            }
        }

        return null;
    }
}