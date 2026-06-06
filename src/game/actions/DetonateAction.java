package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.TremorBeacon;

import java.util.List;

public class DetonateAction extends Action {
    private final int range;

    public DetonateAction(int range) {
        this.range = range;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        List<Location> nearby = map.locationOf(actor).getNearbyLocations(range);
        for (Location location : nearby) {
            List<TremorBeacon> beacons = location.getItemsAs(TremorBeacon.class);
            for (TremorBeacon beacon : beacons) {
                beacon.triggerShockwave(location, map);
            }
        }
        return "Nearby beacons detonated.";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " detonates all nearby beacons.";
    }
}
