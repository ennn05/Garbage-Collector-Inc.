package game.weather;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;

/**
 * A game-turn-aware behaviour that periodically triggers a real-world weather
 * update for the facility map.
 *
 * <p>This is the second higher-level class in the weather pipeline. It depends
 * on {@link WeatherEffect} and {@link WeatherDataExtractor} indirectly through
 * {@link WeatherSystem}, never referencing any concrete implementation —
 * satisfying the Dependency Inversion Principle.
 *
 * <p><b>Scheduling:</b> Every {@value #FETCH_INTERVAL} turns this behaviour
 * locates the first active {@link game.enums.Ability#WORKER} actor on the map
 * (the player), uses their x-coordinate to select the dynamic API query zone,
 * and returns a {@link WeatherAction} that will execute the full weather cycle.
 * On all other turns it returns a {@link DoNothingAction}.
 */
public class WeatherBehaviour implements Behaviour<Actor, Action> {

    /** How many WeatherController turns pass between each API call. */
    private static final int FETCH_INTERVAL = 15;

    private int turnCounter = 0;
    private final WeatherSystem weatherSystem;

    /**
     * @param weatherSystem the system to invoke when the fetch interval elapses
     */
    public WeatherBehaviour(WeatherSystem weatherSystem) {
        this.weatherSystem = weatherSystem;
    }

    /**
     * Called every turn by {@link game.actors.WeatherController}.
     *
     * <p>When the fetch interval is reached, the map is scanned for a WORKER
     * actor. Their location is passed to a new {@link WeatherAction} so that
     * {@link WeatherSystem} can derive the correct API query parameters and
     * centre any area-of-effect weather changes on the player.
     *
     * @param actor    the WeatherController actor (not used for logic)
     * @param location the WeatherController's fixed tile location, used to
     *                 obtain a reference to the current {@link edu.monash.fit2099.engine.positions.GameMap}
     * @return a {@link WeatherAction} every {@value #FETCH_INTERVAL} turns,
     *         otherwise a {@link DoNothingAction}
     */
    @Override
    public Action operate(Actor actor, Location location) {
        turnCounter++;
        if (turnCounter < FETCH_INTERVAL) {
            return new DoNothingAction();
        }
        turnCounter = 0;

        Location playerLocation = findPlayerLocation(location);
        if (playerLocation == null) {
            return new DoNothingAction();
        }

        return new WeatherAction(weatherSystem, playerLocation);
    }

    /**
     * Scans every tile on the map for the first actor with the WORKER ability.
     * This search is necessary because the WeatherController holds no direct
     * reference to any player, keeping the weather system decoupled.
     *
     * @param hint any location on the target map (used to get the map reference)
     * @return the player's location, or null if no worker is present
     */
    private Location findPlayerLocation(Location hint) {
        GameMap map = hint.map();
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.containsAnActor() && loc.getActor().hasAbility(Ability.WORKER)) {
                    return loc;
                }
            }
        }
        return null;
    }
}
