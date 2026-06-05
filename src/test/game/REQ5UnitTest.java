package test.game;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.flora.FleshyMatureTree;
import game.flora.FleshySapling;
import game.flora.FleshySprout;
import game.grounds.Fire;
import game.grounds.Floor;
import game.grounds.FungalGround;
import game.grounds.Puddle;
import game.grounds.ToxicWaste;
import game.grounds.Wall;
import game.weather.WeatherReport;
import game.weather.WeatherReportBuilder;
import game.weather.effects.FungalBloomEffect;
import game.weather.effects.HeatwaveEffect;
import game.weather.effects.StormEffect;
import game.weather.extractors.HumidityExtractor;
import game.weather.extractors.TemperatureExtractor;
import game.weather.extractors.WeatherConditionExtractor;
import game.weather.extractors.WindSpeedExtractor;
import game.weather.zones.AridZone;
import game.weather.zones.HumidZone;
import game.weather.zones.TemperateZone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for Assignment 3 REQ5: Real-World Weather API Integration.
 *
 * Covers: WeatherReportBuilder defaults and setters, all four WeatherDataExtractor
 * implementations (JSON parsing and populateReport), WeatherZone column membership,
 * the passive terrain effects of AridZone / TemperateZone / HumidZone, and the
 * shouldActivate thresholds and apply() effects of all three WeatherEffect classes.
 *
 * API calls are never made in tests — zones and effects are exercised in isolation
 * against an in-memory GameMap.
 */
public class REQ5UnitTest {

    // ── Inner helpers ──────────────────────────────────────────────────────────

    private static class TestWorld extends World {
        TestWorld() { super(new Display()); }
    }

    /**
     * Minimal FungalGround that records whether forceSpread triggered spread().
     * Used to verify HumidZone and FungalBloomEffect without a concrete subclass.
     */
    private static class TestFungalGround extends FungalGround {
        boolean spreadCalled = false;

        TestFungalGround() { super('F', "Test Fungal Ground"); }

        @Override
        protected void onActorPresent(Actor actor, Location location) {}

        @Override
        protected void spread(Location location) { spreadCalled = true; }
    }

    // ── Map factory ────────────────────────────────────────────────────────────

    private GameMap createTestMap(String name, List<String> lines) throws GameEngineException {
        DefaultGroundCreator creator = new DefaultGroundCreator();
        creator.registerGround('.', Floor::new);
        creator.registerGround('#', Wall::new);
        GameMap map = new GameMap(name, creator, lines);
        new TestWorld().addGameMap(map);
        return map;
    }

    /**
     * 15 × 15 open Floor map — large enough for a radius-6 zone effect centred
     * on (7, 7), with room to place tiles outside the radius at x = 0 or x = 14.
     */
    private GameMap createLargeMap() throws GameEngineException {
        String row = "...............";
        return createTestMap("large-map", Arrays.asList(
                row, row, row, row, row, row, row, row,
                row, row, row, row, row, row, row
        ));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // WeatherReportBuilder
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    public void typicalWeatherReportBuilderHasCorrectDefaults() {
        WeatherReport report = new WeatherReportBuilder().build();

        Assertions.assertEquals(20.0,    report.getTemperature(), 0.001);
        Assertions.assertEquals(50,      report.getHumidity());
        Assertions.assertEquals(5.0,     report.getWindSpeed(),   0.001);
        Assertions.assertEquals("Clear", report.getCondition());
    }

    @Test
    public void typicalWeatherReportBuilderAppliesAllSetters() {
        WeatherReportBuilder builder = new WeatherReportBuilder();
        builder.setTemperature(35.0);
        builder.setHumidity(80);
        builder.setWindSpeed(12.0);
        builder.setCondition("Thunderstorm");
        WeatherReport report = builder.build();

        Assertions.assertEquals(35.0,            report.getTemperature(), 0.001);
        Assertions.assertEquals(80,              report.getHumidity());
        Assertions.assertEquals(12.0,            report.getWindSpeed(),   0.001);
        Assertions.assertEquals("Thunderstorm",  report.getCondition());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // WeatherDataExtractor — JSON parsing and populateReport
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    public void typicalTemperatureExtractorParsesValueFromJson() {
        String json = "{\"main\":{\"temp\":28.5,\"humidity\":60}}";
        Assertions.assertEquals(28.5, new TemperatureExtractor().extract(json), 0.001);
    }

    @Test
    public void edgeTemperatureExtractorReturnsDefaultWhenJsonIsNull() {
        Assertions.assertEquals(20.0, new TemperatureExtractor().extract(null), 0.001);
    }

    @Test
    public void typicalHumidityExtractorParsesValueFromJson() {
        String json = "{\"main\":{\"temp\":20.0,\"humidity\":72}}";
        Assertions.assertEquals(72, new HumidityExtractor().extract(json));
    }

    @Test
    public void edgeHumidityExtractorReturnsDefaultWhenJsonIsNull() {
        Assertions.assertEquals(50, new HumidityExtractor().extract(null));
    }

    @Test
    public void typicalWindSpeedExtractorParsesValueFromJson() {
        String json = "{\"wind\":{\"speed\":9.2}}";
        Assertions.assertEquals(9.2, new WindSpeedExtractor().extract(json), 0.001);
    }

    @Test
    public void edgeWindSpeedExtractorReturnsDefaultWhenJsonIsNull() {
        Assertions.assertEquals(5.0, new WindSpeedExtractor().extract(null), 0.001);
    }

    @Test
    public void typicalWeatherConditionExtractorParsesValueFromJson() {
        String json = "{\"weather\":[{\"id\":800,\"main\":\"Rain\",\"description\":\"light rain\"}]}";
        Assertions.assertEquals("Rain", new WeatherConditionExtractor().extract(json));
    }

    @Test
    public void edgeWeatherConditionExtractorReturnsDefaultWhenJsonIsNull() {
        Assertions.assertEquals("Clear", new WeatherConditionExtractor().extract(null));
    }

    @Test
    public void typicalAllExtractorsPopulateReportBuilderCorrectly() {
        String json = "{\"main\":{\"temp\":33.0,\"humidity\":80},"
                    + "\"wind\":{\"speed\":10.5},"
                    + "\"weather\":[{\"main\":\"Thunderstorm\"}]}";
        WeatherReportBuilder builder = new WeatherReportBuilder();
        new TemperatureExtractor().populateReport(builder, json);
        new HumidityExtractor().populateReport(builder, json);
        new WindSpeedExtractor().populateReport(builder, json);
        new WeatherConditionExtractor().populateReport(builder, json);
        WeatherReport report = builder.build();

        Assertions.assertEquals(33.0,            report.getTemperature(), 0.001);
        Assertions.assertEquals(80,              report.getHumidity());
        Assertions.assertEquals(10.5,            report.getWindSpeed(),   0.001);
        Assertions.assertEquals("Thunderstorm",  report.getCondition());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // WeatherZone — containsPlayerX column boundaries
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    public void typicalAridZoneContainsColumnsZeroToNineteen() {
        AridZone zone = new AridZone();
        Assertions.assertTrue(zone.containsPlayerX(0));
        Assertions.assertTrue(zone.containsPlayerX(10));
        Assertions.assertTrue(zone.containsPlayerX(19));
    }

    @Test
    public void edgeAridZoneRejectsColumnsOutsideItsRange() {
        AridZone zone = new AridZone();
        Assertions.assertFalse(zone.containsPlayerX(-1));
        Assertions.assertFalse(zone.containsPlayerX(20));
    }

    @Test
    public void typicalTemperateZoneContainsTwentyToThirtyNine() {
        TemperateZone zone = new TemperateZone();
        Assertions.assertTrue(zone.containsPlayerX(20));
        Assertions.assertTrue(zone.containsPlayerX(30));
        Assertions.assertTrue(zone.containsPlayerX(39));
    }

    @Test
    public void edgeTemperateZoneRejectsColumnsOutsideItsRange() {
        TemperateZone zone = new TemperateZone();
        Assertions.assertFalse(zone.containsPlayerX(19));
        Assertions.assertFalse(zone.containsPlayerX(40));
    }

    @Test
    public void typicalHumidZoneContainsFourtyToFiftyNine() {
        HumidZone zone = new HumidZone();
        Assertions.assertTrue(zone.containsPlayerX(40));
        Assertions.assertTrue(zone.containsPlayerX(50));
        Assertions.assertTrue(zone.containsPlayerX(59));
    }

    @Test
    public void edgeHumidZoneRejectsColumnsOutsideItsRange() {
        HumidZone zone = new HumidZone();
        Assertions.assertFalse(zone.containsPlayerX(39));
        Assertions.assertFalse(zone.containsPlayerX(60));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // AridZone — passive terrain effect
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    public void typicalAridZoneEvaporatesPuddleIntoFloor() throws GameEngineException {
        GameMap map = createLargeMap();
        Location playerLocation = map.at(7, 7);
        Location puddleLoc = map.at(7, 8);
        puddleLoc.setGround(new Puddle());

        new AridZone().applyPassiveEffect(map, playerLocation);

        Assertions.assertNull(puddleLoc.getGroundAs(Puddle.class),
                "Puddle within radius should be evaporated");
        Assertions.assertNotNull(puddleLoc.getGroundAs(Floor.class),
                "Evaporated Puddle tile should become Floor");
    }

    @Test
    public void typicalAridZoneCapsPuddleEvaporationAtTwo() throws GameEngineException {
        GameMap map = createLargeMap();
        Location playerLocation = map.at(7, 7);
        List<Location> puddles = Arrays.asList(map.at(7, 8), map.at(7, 9), map.at(7, 10));
        for (Location loc : puddles) { loc.setGround(new Puddle()); }

        new AridZone().applyPassiveEffect(map, playerLocation);

        int remaining = 0;
        for (Location loc : puddles) {
            if (loc.getGroundAs(Puddle.class) != null) remaining++;
        }
        Assertions.assertEquals(1, remaining,
                "3 Puddles placed; cap is 2 evaporations, so exactly 1 should remain");
    }

    @Test
    public void typicalAridZoneToxicWasteDilutionOnlyProducesPuddleOrToxicWaste()
            throws GameEngineException {
        GameMap map = createLargeMap();
        Location playerLocation = map.at(7, 7);
        List<Location> toxics = Arrays.asList(
                map.at(6, 7), map.at(8, 7), map.at(7, 6), map.at(7, 8), map.at(6, 8));
        for (Location loc : toxics) { loc.setGround(new ToxicWaste()); }

        new AridZone().applyPassiveEffect(map, playerLocation);

        for (Location loc : toxics) {
            boolean isPuddle = loc.getGroundAs(Puddle.class)     != null;
            boolean isToxic  = loc.getGroundAs(ToxicWaste.class) != null;
            Assertions.assertTrue(isPuddle || isToxic,
                    "ToxicWaste should only dilute into Puddle or remain ToxicWaste");
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TemperateZone — passive terrain effect
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    public void typicalTemperateZoneAdvancesFleshySproutToFleshySapling() throws GameEngineException {
        GameMap map = createLargeMap();
        Location playerLocation = map.at(7, 7);
        Location sproutLoc = map.at(7, 8);
        sproutLoc.setGround(new FleshySprout());

        new TemperateZone().applyPassiveEffect(map, playerLocation);

        Assertions.assertNull(sproutLoc.getGroundAs(FleshySprout.class),
                "FleshySprout within radius should be advanced");
        Assertions.assertNotNull(sproutLoc.getGroundAs(FleshySapling.class),
                "Advanced Sprout tile should become FleshySapling");
    }

    @Test
    public void typicalTemperateZoneCapsSproutAdvancementAtTwo() throws GameEngineException {
        GameMap map = createLargeMap();
        Location playerLocation = map.at(7, 7);
        List<Location> sprouts = Arrays.asList(map.at(7, 8), map.at(7, 9), map.at(7, 10));
        for (Location loc : sprouts) { loc.setGround(new FleshySprout()); }

        new TemperateZone().applyPassiveEffect(map, playerLocation);

        int remaining = 0;
        for (Location loc : sprouts) {
            if (loc.getGroundAs(FleshySprout.class) != null) remaining++;
        }
        Assertions.assertEquals(1, remaining,
                "3 Sprouts placed; cap is 2 advances, so exactly 1 should remain");
    }

    @Test
    public void typicalTemperateZoneAdvancesFleshySaplingToFleshyMatureTree()
            throws GameEngineException {
        GameMap map = createLargeMap();
        Location playerLocation = map.at(7, 7);
        Location saplingLoc = map.at(7, 8);
        saplingLoc.setGround(new FleshySapling());

        new TemperateZone().applyPassiveEffect(map, playerLocation);

        Assertions.assertNull(saplingLoc.getGroundAs(FleshySapling.class),
                "FleshySapling within radius should be advanced");
        Assertions.assertNotNull(saplingLoc.getGroundAs(FleshyMatureTree.class),
                "Advanced Sapling tile should become FleshyMatureTree");
    }

    @Test
    public void typicalTemperateZoneCapsSaplingAdvancementAtOne() throws GameEngineException {
        GameMap map = createLargeMap();
        Location playerLocation = map.at(7, 7);
        List<Location> saplings = Arrays.asList(map.at(7, 8), map.at(7, 9));
        for (Location loc : saplings) { loc.setGround(new FleshySapling()); }

        new TemperateZone().applyPassiveEffect(map, playerLocation);

        int remaining = 0;
        for (Location loc : saplings) {
            if (loc.getGroundAs(FleshySapling.class) != null) remaining++;
        }
        Assertions.assertEquals(1, remaining,
                "2 Saplings placed; cap is 1 advancement, so exactly 1 should remain");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // HumidZone — passive terrain effect
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    public void typicalHumidZoneExtinguishesFireAndRestoresPreviousGround()
            throws GameEngineException {
        GameMap map = createLargeMap();
        Location playerLocation = map.at(7, 7);
        Location fireLoc = map.at(7, 8);
        fireLoc.setGround(new Fire(new Floor()));

        new HumidZone().applyPassiveEffect(map, playerLocation);

        Assertions.assertNull(fireLoc.getGroundAs(Fire.class),
                "Fire within radius should be extinguished by humid air");
        Assertions.assertNotNull(fireLoc.getGroundAs(Floor.class),
                "Extinguished tile should restore to its previous ground (Floor)");
    }

    @Test
    public void typicalHumidZoneCapsFireExtinguishedAtTwo() throws GameEngineException {
        GameMap map = createLargeMap();
        Location playerLocation = map.at(7, 7);
        List<Location> fires = Arrays.asList(map.at(7, 8), map.at(7, 9), map.at(7, 10));
        for (Location loc : fires) { loc.setGround(new Fire(new Floor())); }

        new HumidZone().applyPassiveEffect(map, playerLocation);

        int remaining = 0;
        for (Location loc : fires) {
            if (loc.getGroundAs(Fire.class) != null) remaining++;
        }
        Assertions.assertEquals(1, remaining,
                "3 fires placed; cap is 2 extinguished, so exactly 1 should remain");
    }

    @Test
    public void typicalHumidZoneForceSpreadsFungalGroundTileInRadius()
            throws GameEngineException {
        GameMap map = createLargeMap();
        Location playerLocation = map.at(7, 7);
        TestFungalGround fungal = new TestFungalGround();
        map.at(7, 8).setGround(fungal);

        new HumidZone().applyPassiveEffect(map, playerLocation);

        Assertions.assertTrue(fungal.spreadCalled,
                "HumidZone should call forceSpread() on FungalGround tiles within radius");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // WeatherEffect — shouldActivate thresholds
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    public void typicalHeatwaveEffectActivatesWhenTemperatureAtOrAboveThreshold() {
        HeatwaveEffect effect = new HeatwaveEffect();
        Assertions.assertTrue(effect.shouldActivate(new WeatherReport(32.0, 50, 5.0, "Clear")));
        Assertions.assertTrue(effect.shouldActivate(new WeatherReport(40.0, 50, 5.0, "Clear")));
    }

    @Test
    public void edgeHeatwaveEffectDoesNotActivateWhenTemperatureBelowThreshold() {
        HeatwaveEffect effect = new HeatwaveEffect();
        Assertions.assertFalse(effect.shouldActivate(new WeatherReport(31.9, 50, 5.0, "Clear")));
        Assertions.assertFalse(effect.shouldActivate(new WeatherReport(20.0, 50, 5.0, "Clear")));
    }

    @Test
    public void typicalFungalBloomEffectActivatesWhenHumidityAtOrAboveThreshold() {
        FungalBloomEffect effect = new FungalBloomEffect();
        Assertions.assertTrue(effect.shouldActivate(new WeatherReport(20.0, 75, 5.0, "Clear")));
        Assertions.assertTrue(effect.shouldActivate(new WeatherReport(20.0, 90, 5.0, "Clear")));
    }

    @Test
    public void edgeFungalBloomEffectDoesNotActivateWhenHumidityBelowThreshold() {
        FungalBloomEffect effect = new FungalBloomEffect();
        Assertions.assertFalse(effect.shouldActivate(new WeatherReport(20.0, 74, 5.0, "Clear")));
        Assertions.assertFalse(effect.shouldActivate(new WeatherReport(20.0, 50, 5.0, "Clear")));
    }

    @Test
    public void typicalStormEffectActivatesWhenWindSpeedAtOrAboveThreshold() {
        StormEffect effect = new StormEffect();
        Assertions.assertTrue(effect.shouldActivate(new WeatherReport(20.0, 50,  8.0, "Clear")));
        Assertions.assertTrue(effect.shouldActivate(new WeatherReport(20.0, 50, 15.0, "Clear")));
    }

    @Test
    public void edgeStormEffectDoesNotActivateWhenWindSpeedBelowThreshold() {
        StormEffect effect = new StormEffect();
        Assertions.assertFalse(effect.shouldActivate(new WeatherReport(20.0, 50, 7.9, "Clear")));
        Assertions.assertFalse(effect.shouldActivate(new WeatherReport(20.0, 50, 5.0, "Clear")));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // WeatherEffect — apply (physical map changes)
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    public void typicalHeatwaveEffectSpawnsExactlyThreeFireTilesNearPlayer()
            throws GameEngineException {
        GameMap map = createLargeMap();
        Location playerLocation = map.at(7, 7);

        new HeatwaveEffect().apply(map, playerLocation);

        int fireCount = 0;
        for (Location loc : playerLocation.getNearbyLocations(4)) {
            if (loc.getGroundAs(Fire.class) != null) fireCount++;
        }
        Assertions.assertEquals(3, fireCount,
                "Heatwave should ignite exactly 3 Floor tiles (MAX_FIRES cap)");
    }

    @Test
    public void typicalStormEffectSpawnsExactlyThreePuddleTilesNearPlayer()
            throws GameEngineException {
        GameMap map = createLargeMap();
        Location playerLocation = map.at(7, 7);

        new StormEffect().apply(map, playerLocation);

        int puddleCount = 0;
        for (Location loc : playerLocation.getNearbyLocations(5)) {
            if (loc.getGroundAs(Puddle.class) != null) puddleCount++;
        }
        Assertions.assertEquals(3, puddleCount,
                "Storm should create exactly 3 Puddle tiles (MAX_PUDDLES cap)");
    }

    @Test
    public void typicalFungalBloomEffectCallsForceSpreadOnEveryFungalGroundTile()
            throws GameEngineException {
        GameMap map = createLargeMap();
        Location playerLocation = map.at(7, 7);
        TestFungalGround fungal1 = new TestFungalGround();
        TestFungalGround fungal2 = new TestFungalGround();
        map.at(3, 3).setGround(fungal1);
        map.at(11, 11).setGround(fungal2);

        new FungalBloomEffect().apply(map, playerLocation);

        Assertions.assertTrue(fungal1.spreadCalled,
                "FungalBloom should call forceSpread on every FungalGround tile on the map");
        Assertions.assertTrue(fungal2.spreadCalled,
                "FungalBloom should call forceSpread on every FungalGround tile on the map");
    }
}
