package req5;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.FungalGround;
import game.interfaces.FireHazard;
import game.interfaces.PuddleGround;
import game.weather.WeatherReport;
import game.weather.effects.FungalBloomEffect;
import game.weather.effects.HeatwaveEffect;
import game.weather.effects.StormEffect;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the three WeatherEffect implementations: shouldActivate thresholds
 * and the physical map changes made by apply().
 */
class WeatherEffectTest {

    /** Minimal FungalGround stub that records whether forceSpread triggered spread(). */
    private static class TestFungalGround extends FungalGround {
        boolean spreadCalled = false;

        TestFungalGround() { super('F', "Test Fungal Ground"); }

        @Override
        protected void onActorPresent(Actor actor, Location location) {}

        @Override
        protected void spread(Location location) { spreadCalled = true; }
    }

    // ── HeatwaveEffect: threshold ──────────────────────────────────────────────

    /** Verifies that HeatwaveEffect activates exactly at the 32°C threshold. */
    @Test
    void heatwaveActivatesAtThreshold() {
        assertTrue(new HeatwaveEffect().shouldActivate(
                new WeatherReport(32.0, 50, 5.0, "Clear")));
    }

    /** Verifies that HeatwaveEffect activates when temperature is above the threshold. */
    @Test
    void heatwaveActivatesAboveThreshold() {
        assertTrue(new HeatwaveEffect().shouldActivate(
                new WeatherReport(40.0, 50, 5.0, "Clear")));
    }

    /** Verifies that HeatwaveEffect does not activate when temperature is below the threshold. */
    @Test
    void heatwaveDoesNotActivateBelowThreshold() {
        assertFalse(new HeatwaveEffect().shouldActivate(
                new WeatherReport(31.9, 50, 5.0, "Clear")));
    }

    // ── FungalBloomEffect: threshold ───────────────────────────────────────────

    /** Verifies that FungalBloomEffect activates exactly at the 75% humidity threshold. */
    @Test
    void fungalBloomActivatesAtThreshold() {
        assertTrue(new FungalBloomEffect().shouldActivate(
                new WeatherReport(20.0, 75, 5.0, "Clear")));
    }

    /** Verifies that FungalBloomEffect activates when humidity is above the threshold. */
    @Test
    void fungalBloomActivatesAboveThreshold() {
        assertTrue(new FungalBloomEffect().shouldActivate(
                new WeatherReport(20.0, 90, 5.0, "Clear")));
    }

    /** Verifies that FungalBloomEffect does not activate when humidity is below the threshold. */
    @Test
    void fungalBloomDoesNotActivateBelowThreshold() {
        assertFalse(new FungalBloomEffect().shouldActivate(
                new WeatherReport(20.0, 74, 5.0, "Clear")));
    }

    // ── StormEffect: threshold ─────────────────────────────────────────────────

    /** Verifies that StormEffect activates exactly at the 8 m/s wind-speed threshold. */
    @Test
    void stormActivatesAtThreshold() {
        assertTrue(new StormEffect().shouldActivate(
                new WeatherReport(20.0, 50, 8.0, "Clear")));
    }

    /** Verifies that StormEffect activates when wind speed is above the threshold. */
    @Test
    void stormActivatesAboveThreshold() {
        assertTrue(new StormEffect().shouldActivate(
                new WeatherReport(20.0, 50, 15.0, "Clear")));
    }

    /** Verifies that StormEffect does not activate when wind speed is below the threshold. */
    @Test
    void stormDoesNotActivateBelowThreshold() {
        assertFalse(new StormEffect().shouldActivate(
                new WeatherReport(20.0, 50, 7.9, "Clear")));
    }

    // ── HeatwaveEffect: apply ──────────────────────────────────────────────────

    /** Verifies that HeatwaveEffect ignites exactly 3 Floor tiles near the player (MAX_FIRES cap). */
    @Test
    void heatwaveSpawnsExactlyThreeFireTilesNearPlayer() throws Exception {
        GameMap map = new TestWorld().createFloorMap(15, 15);
        Location player = map.at(7, 7);

        new HeatwaveEffect().apply(map, player);

        int fireCount = 0;
        for (Location loc : player.getNearbyLocations(4)) {
            if (loc.getGroundAs(FireHazard.class) != null) fireCount++;
        }
        assertEquals(3, fireCount,
                "Heatwave must ignite exactly 3 Floor tiles (MAX_FIRES cap)");
    }

    // ── StormEffect: apply ─────────────────────────────────────────────────────

    /** Verifies that StormEffect creates exactly 3 Puddle tiles near the player (MAX_PUDDLES cap). */
    @Test
    void stormSpawnsExactlyThreePuddleTilesNearPlayer() throws Exception {
        GameMap map = new TestWorld().createFloorMap(15, 15);
        Location player = map.at(7, 7);

        new StormEffect().apply(map, player);

        int puddleCount = 0;
        for (Location loc : player.getNearbyLocations(5)) {
            if (loc.getGroundAs(PuddleGround.class) != null) puddleCount++;
        }
        assertEquals(3, puddleCount,
                "Storm must create exactly 3 Puddle tiles (MAX_PUDDLES cap)");
    }

    // ── FungalBloomEffect: apply ───────────────────────────────────────────────

    /** Verifies that FungalBloomEffect calls forceSpread on every FungalGround tile on the map. */
    @Test
    void fungalBloomCallsForceSpreadOnEveryFungalGroundTileOnMap() throws Exception {
        GameMap map = new TestWorld().createFloorMap(15, 15);
        Location player = map.at(7, 7);
        TestFungalGround fungal1 = new TestFungalGround();
        TestFungalGround fungal2 = new TestFungalGround();
        map.at(3, 3).setGround(fungal1);
        map.at(11, 11).setGround(fungal2);

        new FungalBloomEffect().apply(map, player);

        assertTrue(fungal1.spreadCalled,
                "FungalBloom must call forceSpread on every FungalGround tile on the map");
        assertTrue(fungal2.spreadCalled,
                "FungalBloom must call forceSpread on every FungalGround tile on the map");
    }
}
