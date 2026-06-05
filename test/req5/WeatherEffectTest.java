package req5;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Fire;
import game.grounds.FungalGround;
import game.grounds.Puddle;
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

    @Test
    void heatwaveActivatesAtThreshold() {
        HeatwaveEffect effect = new HeatwaveEffect();
        assertTrue(effect.shouldActivate(new WeatherReport(32.0, 50, 5.0, "Clear")));
    }

    @Test
    void heatwaveActivatesAboveThreshold() {
        HeatwaveEffect effect = new HeatwaveEffect();
        assertTrue(effect.shouldActivate(new WeatherReport(40.0, 50, 5.0, "Clear")));
    }

    @Test
    void heatwaveDoesNotActivateBelowThreshold() {
        HeatwaveEffect effect = new HeatwaveEffect();
        assertFalse(effect.shouldActivate(new WeatherReport(31.9, 50, 5.0, "Clear")));
    }

    // ── FungalBloomEffect: threshold ───────────────────────────────────────────

    @Test
    void fungalBloomActivatesAtThreshold() {
        FungalBloomEffect effect = new FungalBloomEffect();
        assertTrue(effect.shouldActivate(new WeatherReport(20.0, 75, 5.0, "Clear")));
    }

    @Test
    void fungalBloomActivatesAboveThreshold() {
        FungalBloomEffect effect = new FungalBloomEffect();
        assertTrue(effect.shouldActivate(new WeatherReport(20.0, 90, 5.0, "Clear")));
    }

    @Test
    void fungalBloomDoesNotActivateBelowThreshold() {
        FungalBloomEffect effect = new FungalBloomEffect();
        assertFalse(effect.shouldActivate(new WeatherReport(20.0, 74, 5.0, "Clear")));
    }

    // ── StormEffect: threshold ─────────────────────────────────────────────────

    @Test
    void stormActivatesAtThreshold() {
        StormEffect effect = new StormEffect();
        assertTrue(effect.shouldActivate(new WeatherReport(20.0, 50, 8.0, "Clear")));
    }

    @Test
    void stormActivatesAboveThreshold() {
        StormEffect effect = new StormEffect();
        assertTrue(effect.shouldActivate(new WeatherReport(20.0, 50, 15.0, "Clear")));
    }

    @Test
    void stormDoesNotActivateBelowThreshold() {
        StormEffect effect = new StormEffect();
        assertFalse(effect.shouldActivate(new WeatherReport(20.0, 50, 7.9, "Clear")));
    }

    // ── HeatwaveEffect: apply ──────────────────────────────────────────────────

    @Test
    void heatwaveSpawnsExactlyThreeFireTilesNearPlayer() throws Exception {
        GameMap map = new TestWorld().createFloorMap(15, 15);
        Location player = map.at(7, 7);

        new HeatwaveEffect().apply(map, player);

        int fireCount = 0;
        for (Location loc : player.getNearbyLocations(4)) {
            if (loc.getGroundAs(Fire.class) != null) fireCount++;
        }
        assertEquals(3, fireCount,
                "Heatwave must ignite exactly 3 Floor tiles (MAX_FIRES cap)");
    }

    // ── StormEffect: apply ─────────────────────────────────────────────────────

    @Test
    void stormSpawnsExactlyThreePuddleTilesNearPlayer() throws Exception {
        GameMap map = new TestWorld().createFloorMap(15, 15);
        Location player = map.at(7, 7);

        new StormEffect().apply(map, player);

        int puddleCount = 0;
        for (Location loc : player.getNearbyLocations(5)) {
            if (loc.getGroundAs(Puddle.class) != null) puddleCount++;
        }
        assertEquals(3, puddleCount,
                "Storm must create exactly 3 Puddle tiles (MAX_PUDDLES cap)");
    }

    // ── FungalBloomEffect: apply ───────────────────────────────────────────────

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
