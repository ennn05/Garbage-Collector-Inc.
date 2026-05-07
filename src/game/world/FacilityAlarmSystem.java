package game.world;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.Unlockable;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Map-level facility alarm system for handling alarm state and lockdown.
 */
public class FacilityAlarmSystem {
    private static final int LOCKDOWN_DURATION = 3;
    private static final Map<GameMap, FacilityAlarmSystem> REGISTRY = new WeakHashMap<>(); // dependent alarm each map

    private final GameMap map;
    private int remainingAlarmTurns = 0;
    private Location controllerTrapLocation;      // choose a trap as a timer

    private FacilityAlarmSystem(GameMap map) {
        this.map = map;
    }

    public static FacilityAlarmSystem register(GameMap map) {
        FacilityAlarmSystem system = new FacilityAlarmSystem(map);
        REGISTRY.put(map, system);
        return system;
    }

    public static FacilityAlarmSystem forMap(GameMap map) {
        return REGISTRY.get(map);
    }

    public void triggerAlarm() {
        remainingAlarmTurns = LOCKDOWN_DURATION;
        relockAllDoors();
    }

    public boolean isAlarmActive() {
        return remainingAlarmTurns > 0;
    }

    public boolean isLockdownActive() {
        return remainingAlarmTurns > 0;
    }

    public void tickFromTrap(Location trapLocation) {
        if (controllerTrapLocation == null) {
            controllerTrapLocation = trapLocation;
        }

        if (!sameCoordinate(controllerTrapLocation, trapLocation)) {
            return;                                                // no -rounds if not control trap
        }

        if (remainingAlarmTurns > 0) {
            remainingAlarmTurns--;
        }
    }

    private boolean sameCoordinate(Location a, Location b) {
        return a.map() == b.map() && a.x() == b.x() && a.y() == b.y(); // Justify same location or not
    }

    private void relockAllDoors() {
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Unlockable unlockable = map.at(x, y).getGroundAs(Unlockable.class);
                if (unlockable != null) {
                    unlockable.lock();
                }
            }
        }
    }

    public boolean canDoorsBeUnlocked() {
        return !isLockdownActive(); // allow open door only when !lockdown
    }

    public int getRemainingAlarmTurns() {
        return remainingAlarmTurns; // give other class method to count remain alarm turns
    }
}