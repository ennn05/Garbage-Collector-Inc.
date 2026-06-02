package game.status;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Status applied to a Scrap Snatcher after it is infected by a Parasite.
 * <p>
 * The infected snatcher loses one hit point every turn until it dies. The status
 * remains active while the infected snatcher is still conscious and still exists
 * on the map.
 */
public class InfectedSnatcherStatus implements Status {
    private static final int DAMAGE_PER_TURN = 1;

    private final Actor infectedSnatcher;
    private final Display display = new Display();
    private boolean active = true;

    /**
     * Constructor.
     *
     * @param infectedSnatcher the Scrap Snatcher affected by this status
     */
    public InfectedSnatcherStatus(Actor infectedSnatcher) {
        this.infectedSnatcher = infectedSnatcher;
    }

    /**
     * Damages the infected snatcher once per turn and removes it when it dies.
     *
     * @param currEntity the entity this status is attached to
     * @param location the current location of the infected snatcher
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        if (!active || !location.map().contains(infectedSnatcher)) {
            active = false;
            return;
        }

        infectedSnatcher.hurt(DAMAGE_PER_TURN);
        display.println(infectedSnatcher + " suffers " + DAMAGE_PER_TURN
                + " damage from the infection.");

        if (!infectedSnatcher.isConscious()) {
            display.println(infectedSnatcher + " dies from the infection.");
            location.map().removeActor(infectedSnatcher);
            active = false;
        }
    }

    /**
     * Checks whether this infection status is still active.
     *
     * @return true while the infected snatcher is still alive
     */
    @Override
    public boolean isStatusActive() {
        return active;
    }
}