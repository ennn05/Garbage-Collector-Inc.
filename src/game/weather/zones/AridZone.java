package game.weather.zones;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Floor;
import game.grounds.Puddle;
import game.interfaces.PuddleGround;
import game.interfaces.ToxicGround;
import game.weather.WeatherZone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The Arid Zone covers map columns 0–19, linked to Melbourne, Australia.
 *
 * <p><b>Passive climate effect — Desiccation:</b>
 * Melbourne's hot, dry summers impose a relentless drying effect on the facility:
 * <ol>
 *   <li>Up to 2 {@link Puddle} tiles within a 6-tile radius of the player are
 *       converted to {@link Floor}. Standing moisture evaporates, countering any
 *       puddles created by {@link game.weather.effects.StormEffect} and making
 *       the facility progressively drier over time.</li>
 *   <li>Each {@link ToxicWaste} tile in the same radius has a
 *       {@value #DILUTION_CHANCE}% chance to be converted to a {@link Puddle}.
 *       Extreme heat partially neutralises chemical runoff into a less dangerous
 *       (but still present) liquid hazard.</li>
 * </ol>
 *
 * <p>The net result creates a strategic tension: storm puddles are consumed by the
 * arid climate, while toxic zones are slowly softened — trading one danger for another.
 */
public class AridZone extends WeatherZone {

    private static final String API_BASE =
            "https://api.openweathermap.org/data/2.5/weather";
    private static final double LAT = -37.81;
    private static final double LON = 144.96;

    private static final int MIN_X = 0;
    private static final int MAX_X = 19;
    private static final int RADIUS = 6;
    private static final int MAX_EVAPORATE = 2;
    private static final double DILUTION_CHANCE = 0.30;

    private final Random random = new Random();

    @Override
    public String buildApiQuery(String apiKey) {
        return String.format("%s?lat=%.2f&lon=%.2f&units=metric&appid=%s",
                API_BASE, LAT, LON, apiKey);
    }

    @Override
    public boolean containsPlayerX(int playerX) {
        return playerX >= MIN_X && playerX <= MAX_X;
    }

    /**
     * Applies desiccation: evaporates Puddle tiles and partially dilutes ToxicWaste.
     *
     * @param map            the game map to modify
     * @param playerLocation location used as the centre of the effect radius
     */
    @Override
    public void applyPassiveEffect(GameMap map, Location playerLocation) {
        List<Location> puddles = new ArrayList<>();
        List<Location> toxics  = new ArrayList<>();

        for (Location loc : playerLocation.getNearbyLocations(RADIUS)) {
            if (loc.getGroundAs(PuddleGround.class) != null) {
                puddles.add(loc);
            } else if (loc.getGroundAs(ToxicGround.class) != null) {
                toxics.add(loc);
            }
        }

        // Evaporate up to MAX_EVAPORATE puddles
        Collections.shuffle(puddles);
        int evaporated = 0;
        for (Location loc : puddles) {
            if (evaporated >= MAX_EVAPORATE) break;
            loc.setGround(new Floor());
            System.out.println("Arid heat evaporates a puddle at " + loc + ".");
            evaporated++;
        }

        // 30% chance per ToxicWaste to dilute into a Puddle
        for (Location loc : toxics) {
            if (random.nextDouble() < DILUTION_CHANCE) {
                loc.setGround(new Puddle());
                System.out.println("Arid heat dilutes toxic waste into a puddle at " + loc + ".");
            }
        }
    }

    @Override
    public String getZoneName() {
        return "Arid Zone (Melbourne)";
    }
}
