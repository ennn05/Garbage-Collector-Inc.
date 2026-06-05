package game.weather.zones;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Fire;
import game.grounds.FungalGround;
import game.weather.WeatherZone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Humid Zone covers map columns 40–59, linked to Tokyo, Japan.
 *
 * <p><b>Passive climate effect — Fire Suppression and Fungal Amplification:</b>
 * Tokyo's high ambient humidity creates a dual environmental shift:
 * <ol>
 *   <li>Up to 2 {@link Fire} tiles within a 6-tile radius of the player are
 *       extinguished — the moisture saturating the air prevents combustion from
 *       sustaining. Each fire is resolved by restoring the ground it was wrapping
 *       (via {@link Fire#getPreviousGround()}), so the original terrain is recovered
 *       rather than replaced with a generic Floor.</li>
 *   <li>Up to 2 {@link FungalGround} tiles in the same radius have
 *       {@link FungalGround#forceSpread(Location)} called on them immediately,
 *       bypassing their normal 10-turn passive spread timer. The same moisture that
 *       kills fire feeds fungal colonisation.</li>
 * </ol>
 *
 * <p>The net result is a unique dual threat: fire-based defences are systematically
 * dismantled while the fungal infection accelerates simultaneously, forcing the player
 * to cope with a rapidly expanding biological hazard unprotected by fire barriers.
 */
public class HumidZone extends WeatherZone {

    private static final String API_BASE =
            "https://api.openweathermap.org/data/2.5/weather";
    private static final double LAT = 35.68;
    private static final double LON = 139.69;

    private static final int MIN_X = 40;
    private static final int MAX_X = 59;
    private static final int RADIUS = 6;
    private static final int MAX_FIRES_EXTINGUISHED = 2;
    private static final int MAX_FUNGAL_SPREAD = 2;

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
     * Applies fire suppression and fungal amplification in the player's vicinity.
     *
     * @param map            the game map to modify
     * @param playerLocation location used as the centre of the effect radius
     */
    @Override
    public void applyPassiveEffect(GameMap map, Location playerLocation) {
        List<Location> fireTiles   = new ArrayList<>();
        List<Location> fungalTiles = new ArrayList<>();

        for (Location loc : playerLocation.getNearbyLocations(RADIUS)) {
            if (loc.getGround() instanceof Fire) {
                fireTiles.add(loc);
            } else if (loc.getGroundAs(FungalGround.class) != null) {
                fungalTiles.add(loc);
            }
        }

        // Extinguish up to MAX_FIRES_EXTINGUISHED fires by restoring inner ground
        Collections.shuffle(fireTiles);
        int extinguished = 0;
        for (Location loc : fireTiles) {
            if (extinguished >= MAX_FIRES_EXTINGUISHED) break;
            Fire fire = (Fire) loc.getGround();
            loc.setGround(fire.getPreviousGround());
            System.out.println("Humid air extinguishes the fire at " + loc + ", restoring the ground beneath.");
            extinguished++;
        }

        // Force-spread up to MAX_FUNGAL_SPREAD fungal tiles
        Collections.shuffle(fungalTiles);
        int spread = 0;
        for (Location loc : fungalTiles) {
            if (spread >= MAX_FUNGAL_SPREAD) break;
            FungalGround fg = loc.getGroundAs(FungalGround.class);
            if (fg != null) {
                fg.forceSpread(loc);
                System.out.println("Humidity accelerates fungal growth at " + loc + ".");
                spread++;
            }
        }
    }

    @Override
    public String getZoneName() {
        return "Humid Zone (Tokyo)";
    }
}
