package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Unlockable;
import game.world.FacilityAlarmSystem;

/**
 * The bureaucratic process of asking a piece of the environment for permission to pass.
 */
public class UnlockDoorAction extends Action {

    /**
     * When executed, it will search for a nearby locked unlockable ground and unlock it.
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

            if (unlockable != null) {
                if (!unlockable.isUnlocked()) {
                    unlockable.unlock();
                    return actor + " unlocked " + surroundingLocation.getGround() + " at " + surroundingLocation;
                }
            }
        }

        return "There is no locked door to unlock.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " unlocks door";
    }
}