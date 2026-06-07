package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;
import game.world.FacilityAlarmSystem;

/**
 * A trap tile that triggers the facility alarm when a worker steps onto it.
 */
public class Trap extends Ground {
    private boolean workerWasHereLastTurn = false;

    public Trap() {
        super('!', "Trap");
    }

    @Override
    public void tick(Location location) {
        FacilityAlarmSystem alarmSystem = FacilityAlarmSystem.forMap(location.map());

        if (alarmSystem != null) {
            alarmSystem.tickFromTrap(location);
        }

        boolean workerCurrentlyHere = false;

        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            workerCurrentlyHere = actor.hasAbility(Ability.WORKER);
        }

        if (workerCurrentlyHere && !workerWasHereLastTurn && alarmSystem != null) {
            alarmSystem.triggerAlarm();
            new Display().println("Facility alarm triggered! All doors are locked for 3 turns.");
        }

        workerWasHereLastTurn = workerCurrentlyHere;
    }
}