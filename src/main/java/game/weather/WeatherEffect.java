package game.weather;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents a single environmental consequence of a weather condition.
 *
 * Each implementation decides its own activation threshold and applies a
 * concrete physical change to the game world (spawning/modifying/destroying
 * game objects).
 *
 * Higher-level classes ({@link WeatherBehaviour}) depend solely on this
 * interface, never on concrete implementations (Dependency Inversion Principle).
 */
public interface WeatherEffect {

    /**
     * Apply this effect to the game map, centred on the player's location.
     *
     * @param map            the map to modify
     * @param playerLocation the current location of the player
     */
    void apply(GameMap map, Location playerLocation);

    /**
     * Returns true when the weather data in the given report crosses the
     * threshold that triggers this effect.
     *
     * @param report the latest parsed weather data
     * @return true if this effect should be applied
     */
    boolean shouldActivate(WeatherReport report);

    /**
     * A short human-readable label used in console output.
     *
     * @return the effect name
     */
    String getEffectName();
}
