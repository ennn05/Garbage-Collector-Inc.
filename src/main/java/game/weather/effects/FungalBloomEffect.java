package game.weather.effects;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.FungalGround;
import game.weather.WeatherEffect;
import game.weather.WeatherReport;

/**
 * Activates when humidity reaches or exceeds {@value #THRESHOLD}%.
 *
 * Forces every {@link FungalGround} tile on the map to spread immediately,
 * bypassing the normal 10-turn passive spread timer. This dramatically
 * accelerates fungal colonisation of the facility.
 */
public class FungalBloomEffect implements WeatherEffect {

    private static final int THRESHOLD = 75;

    /**
     * Iterates every tile on the map and calls {@link FungalGround#forceSpread(Location)}
     * on each {@link FungalGround} tile, bypassing the normal spread timer.
     *
     * @param map            the game map to iterate
     * @param playerLocation unused — effect is map-wide rather than player-centred
     */
    @Override
    public void apply(GameMap map, Location playerLocation) {
        int bloomed = 0;
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                FungalGround fg = loc.getGroundAs(FungalGround.class);
                if (fg != null) {
                    fg.forceSpread(loc);
                    bloomed++;
                }
            }
        }
        if (bloomed > 0) {
            System.out.println("High humidity triggers a fungal bloom! " + bloomed + " fungal tile(s) spread instantly.");
        }
    }

    /**
     * Activates when the reported relative humidity is at or above {@value #THRESHOLD}%.
     *
     * @param report the current weather data snapshot
     * @return {@code true} if the humidity threshold is met
     */
    @Override
    public boolean shouldActivate(WeatherReport report) {
        return report.getHumidity() >= THRESHOLD;
    }

    /**
     * Returns a human-readable name for this effect.
     *
     * @return {@code "Fungal Bloom (all fungal tiles spread instantly)"}
     */
    @Override
    public String getEffectName() {
        return "Fungal Bloom (all fungal tiles spread instantly)";
    }
}
