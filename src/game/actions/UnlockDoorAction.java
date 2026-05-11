package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Door;
import game.interfaces.DoorUnlocker;
import game.interfaces.Unlockable;
import game.items.AccessCard;
import game.world.FacilityAlarmSystem;

/**
 * An action that unlocks a nearby unlockable ground using a door unlocker.
 */
public class UnlockDoorAction extends Action {
    private final DoorUnlocker doorUnlocker;

    /**
     * Constructor.
     *
     * @param doorUnlocker the object used to unlock a door
     */
    public UnlockDoorAction(DoorUnlocker doorUnlocker) {
        this.doorUnlocker = doorUnlocker;
    }

    /**
     * When executed, it searches for a nearby locked unlockable ground and unlocks it.
     *
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return the description of the result of opening a door
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        FacilityAlarmSystem alarmSystem = FacilityAlarmSystem.forMap(map);

        if (alarmSystem != null && !alarmSystem.canDoorsBeUnlocked()) {
            return "The facility alarm is active. Doors remain locked for another "
                    + alarmSystem.getRemainingAlarmTurns() + " turn(s).";
        }

        Location currentLocation = map.locationOf(actor);

        for (Exit exit : currentLocation.getExits()) {
            Location surroundingLocation = exit.getDestination();
            Unlockable unlockable = surroundingLocation.getGroundAs(Unlockable.class);

            if (unlockable != null && doorUnlocker.canUnlock(unlockable)) {
                // For Doors, check clearance and execute unlock effects
                if (unlockable instanceof Door) {
                    Door door = (Door) unlockable;
                    
                    // Check if actor has a card with sufficient clearance
                    AccessCard card = findAccessCard(actor, door);
                    if (card == null) {
                        return "Insufficient clearance to unlock " + surroundingLocation.getGround() + ".";
                    }
                    
                    // Execute the unlock
                    doorUnlocker.unlock(unlockable);
                    door.onUnlock(actor);
                    return actor + " unlocked " + surroundingLocation.getGround() + " at " + surroundingLocation;
                } else {
                    // For other unlockables, just unlock them
                    doorUnlocker.unlock(unlockable);
                    return actor + " unlocked " + surroundingLocation.getGround() + " at " + surroundingLocation;
                }
            }
        }

        return "There is no locked door this item can unlock.";
    }

    /**
     * Find an access card in the actor's inventory with sufficient clearance for the door.
     * @param actor the actor to check
     * @param door the door that needs to be unlocked
     * @return the access card with sufficient clearance, or null if not found
     */
    private AccessCard findAccessCard(Actor actor, Door door) {
        for (Object item : actor.getInventory().getItems()) {
            if (item instanceof AccessCard) {
                AccessCard card = (AccessCard) item;
                if (card.getLevel().getWeight() >= door.getRequiredClearance().getWeight()) {
                    return card;
                }
            }
        }
        return null;
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " unlocks a nearby door";
    }
}