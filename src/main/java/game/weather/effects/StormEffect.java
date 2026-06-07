package game.weather.effects;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Puddle;
import game.interfaces.Seedable;
import game.weather.WeatherEffect;
import game.weather.WeatherReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Activates when wind speed reaches or exceeds {@value #THRESHOLD} m/s.
 *
 * Scatters moisture across the facility floor: converts up to
 * {@value #MAX_PUDDLES} random Floor tiles within a {@value #RADIUS}-tile
 * radius of the player into {@link Puddle} ground. Puddles can poison
 * actors who drink from them (unless sterilised), adding a new environmental
 * hazard driven by the real-world wind conditions at the player's mapped zone.
 */
public class StormEffect implements WeatherEffect {

    private static final double THRESHOLD = 8.0;
    private static final int RADIUS = 5;
    private static final int MAX_PUDDLES = 3;

    @Override
    public void apply(GameMap map, Location playerLocation) {
        List<Location> floorTiles = new ArrayList<>();
        for (Location loc : playerLocation.getNearbyLocations(RADIUS)) {
            if (loc.getGroundAs(Seedable.class) != null) {
                floorTiles.add(loc);
            }
        }

        Collections.shuffle(floorTiles);
        int count = Math.min(MAX_PUDDLES, floorTiles.size());
        for (int i = 0; i < count; i++) {
            floorTiles.get(i).setGround(new Puddle());
            System.out.println("Storm winds scatter moisture — a puddle forms at " + floorTiles.get(i) + "!");
        }
    }

    @Override
    public boolean shouldActivate(WeatherReport report) {
        return report.getWindSpeed() >= THRESHOLD;
    }

    @Override
    public String getEffectName() {
        return "Storm (puddles spread near player)";
    }
}
