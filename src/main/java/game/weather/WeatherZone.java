package game.weather;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Abstract base for a geographic climate region of the facility map.
 *
 * <p>Each concrete subclass is responsible for three things:
 * <ol>
 *   <li><b>Geographic identity</b> — it owns the real-world city coordinates that
 *       determine the dynamic API query URL ({@link #buildApiQuery}).</li>
 *   <li><b>Player-range detection</b> — it knows which column range of the
 *       facility map it covers ({@link #containsPlayerX}).</li>
 *   <li><b>Passive climate effect</b> — it applies a complex, always-active
 *       terrain transformation to the map every weather cycle, independent of
 *       any numeric threshold ({@link #applyPassiveEffect}). The effect is unique
 *       to each zone's real-world climate character.</li>
 * </ol>
 *
 * <p>The passive effect is what distinguishes {@code WeatherZone} implementations
 * from {@link WeatherEffect} implementations: zone effects are permanent baseline
 * climate behaviour, whereas {@link WeatherEffect}s are spike events triggered
 * only when live weather data crosses a danger threshold.
 *
 * <p>Higher-level classes ({@link WeatherSystem}) depend only on this abstraction,
 * never on any concrete subclass — satisfying the Dependency Inversion Principle.
 */
public abstract class WeatherZone {

    /**
     * Builds the full OpenWeatherMap API query URL for this zone's linked city.
     * The URL encodes the city's real latitude and longitude, making the API
     * request dynamically driven by which zone the player currently occupies.
     *
     * @param apiKey the OpenWeatherMap API key (never hard-coded in subclasses)
     * @return the complete URL string ready for {@code HttpURLConnection}
     */
    public abstract String buildApiQuery(String apiKey);

    /**
     * Returns {@code true} when the player's current x-coordinate falls within
     * this zone's column range on the facility map.
     *
     * @param playerX the active player's x-coordinate
     * @return whether this zone is currently active
     */
    public abstract boolean containsPlayerX(int playerX);

    /**
     * Applies this zone's passive climate effect to the game map.
     *
     * <p>This method is called every weather cycle, regardless of what the
     * live weather data says. Each concrete implementation physically alters
     * game objects (terrain tiles, actor states) in a way that reflects the
     * real-world climate character of the zone's linked city.
     *
     * @param map            the game map to modify
     * @param playerLocation the current location of the active player, used to
     *                       centre area-of-effect scanning
     */
    public abstract void applyPassiveEffect(GameMap map, Location playerLocation);

    /**
     * Returns a human-readable label for this zone used in console output.
     *
     * @return the zone's display name (e.g., "Arid Zone (Melbourne)")
     */
    public abstract String getZoneName();
}
