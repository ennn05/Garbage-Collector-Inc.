package game;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * The bureaucratic process of asking a piece of the environment for permission to pass.
 * Now checks clearance levels and applies door-specific unlock effects.
 */
public class UnlockDoorAction extends Action {

    /**
     * When executed, it will search for a nearby door and attempt to unlock it.
     * Checks the actor's AccessCard clearance against the door's requirements.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return the description of the result of the action of opening a door
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Location currentLocation = map.locationOf(actor);
        
        // Find the actor's AccessCard
        AccessCard card = null;
        for (Item item : actor.getInventory()) {
            if (item instanceof AccessCard) {
                card = (AccessCard) item;
                break;
            }
        }
        
        // If no access card, can't unlock
        if (card == null) {
            return "Access denied. No access card found.";
        }
        
        // Search for a door adjacent to the actor
        for (Exit exit : currentLocation.getExits()) {
            Location surroundingLocation = exit.getDestination();
            Ground surroundingGround = surroundingLocation.getGround();
            
            // Check if it's a door
            if (surroundingGround instanceof Door) {
                Door door = (Door) surroundingGround;
                
                // Check clearance
                if (!card.getClearanceLevel().canOpen(door.getRequiredClearance())) {
                    return "Access denied. Clearance level too low for " + door;
                }
                
                // Clearance granted - apply unlock effect and open door
                door.onUnlock(actor);
                
                // Special handling for IronDoor
                if (door instanceof IronDoor) {
                    ((IronDoor) door).setAdjacentTilesOnFire(surroundingLocation, map);
                }
                
                door.isUnlocked = true;
                return actor + " unlocked " + door + " at " + surroundingLocation;
            }
        }
        return "There is no door to unlock.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " unlocks door";
    }
}
