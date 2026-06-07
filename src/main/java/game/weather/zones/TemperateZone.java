package game.weather.zones;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.flora.FleshyMatureTree;
import game.flora.FleshySapling;
import game.interfaces.FleshySaplingGround;
import game.interfaces.FleshySproutGround;
import game.weather.WeatherZone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Temperate Zone covers map columns 20–39, linked to London, United Kingdom.
 *
 * <p><b>Passive climate effect — Accelerated Flora Growth:</b>
 * London's mild, consistently moist climate acts as a biological accelerant on the
 * facility's already-dangerous flora population:
 * <ol>
 *   <li>Up to 2 {@link FleshySprout} tiles within a 6-tile radius of the player are
 *       immediately advanced to {@link FleshySapling}, skipping their normal 20-turn
 *       growth timer and 25% chance roll.</li>
 *   <li>Up to 1 {@link FleshySapling} tile in the same radius is immediately advanced
 *       to {@link FleshyMatureTree}.</li>
 * </ol>
 *
 * <p>The net result is that the flora threat escalates much faster in the temperate
 * zone. Where a Fleshy Sprout might naturally take 40+ turns to reach maturity,
 * the London climate can push it to maturity in a single weather cycle, dramatically
 * increasing the rate of creature spawning from mature trees.
 */
public class TemperateZone extends WeatherZone {

    private static final String API_BASE =
            "https://api.openweathermap.org/data/2.5/weather";
    private static final double LAT = 51.51;
    private static final double LON = -0.13;

    private static final int MIN_X = 20;
    private static final int MAX_X = 39;
    private static final int RADIUS = 6;
    private static final int MAX_SPROUT_ADVANCE = 2;
    private static final int MAX_SAPLING_ADVANCE = 1;

    /**
     * Builds the OpenWeatherMap API URL for London (lat {@value #LAT}, lon {@value #LON}).
     *
     * @param apiKey the OpenWeatherMap API key
     * @return a fully formed URL string with metric units
     */
    @Override
    public String buildApiQuery(String apiKey) {
        return String.format("%s?lat=%.2f&lon=%.2f&units=metric&appid=%s",
                API_BASE, LAT, LON, apiKey);
    }

    /**
     * Returns {@code true} if the player's X coordinate falls within columns
     * {@value #MIN_X}–{@value #MAX_X} (the temperate zone region).
     *
     * @param playerX the player's current X coordinate on the map
     * @return {@code true} if the player is in this zone
     */
    @Override
    public boolean containsPlayerX(int playerX) {
        return playerX >= MIN_X && playerX <= MAX_X;
    }

    /**
     * Applies accelerated flora growth: advances Sprouts → Saplings and Saplings →
     * MatureTrees within range of the player.
     *
     * @param map            the game map to modify
     * @param playerLocation location used as the centre of the effect radius
     */
    @Override
    public void applyPassiveEffect(GameMap map, Location playerLocation) {
        List<Location> sprouts  = new ArrayList<>();
        List<Location> saplings = new ArrayList<>();

        for (Location loc : playerLocation.getNearbyLocations(RADIUS)) {
            if (loc.getGroundAs(FleshySproutGround.class) != null) {
                sprouts.add(loc);
            } else if (loc.getGroundAs(FleshySaplingGround.class) != null) {
                saplings.add(loc);
            }
        }

        // Advance up to MAX_SPROUT_ADVANCE sprouts → saplings
        Collections.shuffle(sprouts);
        int advanced = 0;
        for (Location loc : sprouts) {
            if (advanced >= MAX_SPROUT_ADVANCE) break;
            loc.setGround(new FleshySapling());
            System.out.println("Temperate moisture accelerates a Fleshy Sprout into a Fleshy Sapling at " + loc + ".");
            advanced++;
        }

        // Advance up to MAX_SAPLING_ADVANCE saplings → mature trees
        Collections.shuffle(saplings);
        int matured = 0;
        for (Location loc : saplings) {
            if (matured >= MAX_SAPLING_ADVANCE) break;
            loc.setGround(new FleshyMatureTree());
            System.out.println("Temperate moisture accelerates a Fleshy Sapling into a Fleshy Mature Tree at " + loc + ".");
            matured++;
        }
    }

    /**
     * Returns the display name of this zone.
     *
     * @return {@code "Temperate Zone (London)"}
     */
    @Override
    public String getZoneName() {
        return "Temperate Zone (London)";
    }
}
