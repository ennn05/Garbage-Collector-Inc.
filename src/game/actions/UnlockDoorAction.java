package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.DoorUnlocker;
import game.interfaces.Unlockable;
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
                doorUnlocker.unlock(unlockable);

                String result = actor + " unlocked "
                        + surroundingLocation.getGround()
                        + " at " + surroundingLocation + ".";

                String unlockEffect = unlockable.onUnlocked(actor, surroundingLocation);
                if (!unlockEffect.isEmpty()) {
                    result += "\n" + unlockEffect;
                }

                return result;
            }
        }

        return "There is no locked door this item can unlock.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " unlocks a nearby door";
    }
}