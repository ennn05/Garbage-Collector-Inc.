package game.weather.effects;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Fire;
import game.interfaces.Seedable;
import game.weather.WeatherEffect;
import game.weather.WeatherReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Activates when temperature reaches or exceeds {@value #THRESHOLD}°C.
 *
 * Converts up to {@value #MAX_FIRES} Floor tiles within a 4-tile radius of the
 * player into {@link Fire} ground, physically altering the map and creating new
 * hazards that damage any actor who steps on them.
 */
public class HeatwaveEffect implements WeatherEffect {

    private static final double THRESHOLD = 32.0;
    private static final int RADIUS = 4;
    private static final int MAX_FIRES = 3;

    /**
     * Converts up to {@value #MAX_FIRES} randomly chosen {@link game.interfaces.Seedable}
     * tiles within {@value #RADIUS} tiles of the player into {@link Fire} ground.
     *
     * @param map            the game map to modify
     * @param playerLocation the player's location used as the effect centre
     */
    @Override
    public void apply(GameMap map, Location playerLocation) {
        List<Location> floorTiles = new ArrayList<>();
        for (Location loc : playerLocation.getNearbyLocations(RADIUS)) {
            if (loc.getGroundAs(Seedable.class) != null) {
                floorTiles.add(loc);
            }
        }

        Collections.shuffle(floorTiles);
        int count = Math.min(MAX_FIRES, floorTiles.size());
        for (int i = 0; i < count; i++) {
            Location loc = floorTiles.get(i);
            Ground current = loc.getGround();
            loc.setGround(new Fire(current));
            System.out.println("Heatwave ignites the floor at " + loc + "!");
        }
    }

    /**
     * Activates when the reported temperature is at or above {@value #THRESHOLD}°C.
     *
     * @param report the current weather data snapshot
     * @return {@code true} if the temperature threshold is met
     */
    @Override
    public boolean shouldActivate(WeatherReport report) {
        return report.getTemperature() >= THRESHOLD;
    }

    /**
     * Returns a human-readable name for this effect.
     *
     * @return {@code "Heatwave (fire tiles spawned near player)"}
     */
    @Override
    public String getEffectName() {
        return "Heatwave (fire tiles spawned near player)";
    }
}
