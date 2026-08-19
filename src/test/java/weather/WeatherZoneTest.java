package weather;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Fire;
import game.grounds.Floor;
import game.grounds.FungalGround;
import game.grounds.Puddle;
import game.grounds.ToxicWaste;
import game.flora.FleshySapling;
import game.flora.FleshySprout;
import game.interfaces.FireHazard;
import game.interfaces.FleshyMatureTreeGround;
import game.interfaces.FleshySaplingGround;
import game.interfaces.FleshySproutGround;
import game.interfaces.PuddleGround;
import game.interfaces.Seedable;
import game.interfaces.ToxicGround;
import game.weather.zones.AridZone;
import game.weather.zones.HumidZone;
import game.weather.zones.TemperateZone;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for WeatherZone implementations: column-range membership and
 * passive terrain effects applied to an in-memory GameMap.
 */
class WeatherZoneTest {

    /** Minimal FungalGround stub that records whether forceSpread triggered spread(). */
    private static class TestFungalGround extends FungalGround {
        boolean spreadCalled = false;

        TestFungalGround() { super('F', "Test Fungal Ground"); }

        @Override
        protected void onActorPresent(Actor actor, Location location) {}

        @Override
        protected void spread(Location location) { spreadCalled = true; }
    }

    // ── AridZone: containsPlayerX ──────────────────────────────────────────────

    /** Verifies that AridZone contains columns 0 through 19 (inclusive). */
    @Test
    void aridZoneContainsColumnsZeroToNineteen() {
        AridZone zone = new AridZone();
        assertTrue(zone.containsPlayerX(0));
        assertTrue(zone.containsPlayerX(10));
        assertTrue(zone.containsPlayerX(19));
    }

    /** Verifies that AridZone rejects columns outside the 0–19 range. */
    @Test
    void aridZoneRejectsColumnsOutsideItsRange() {
        AridZone zone = new AridZone();
        assertFalse(zone.containsPlayerX(-1));
        assertFalse(zone.containsPlayerX(20));
    }

    // ── TemperateZone: containsPlayerX ────────────────────────────────────────

    /** Verifies that TemperateZone contains columns 20 through 39 (inclusive). */
    @Test
    void temperateZoneContainsTwentyToThirtyNine() {
        TemperateZone zone = new TemperateZone();
        assertTrue(zone.containsPlayerX(20));
        assertTrue(zone.containsPlayerX(30));
        assertTrue(zone.containsPlayerX(39));
    }

    /** Verifies that TemperateZone rejects columns outside the 20–39 range. */
    @Test
    void temperateZoneRejectsColumnsOutsideItsRange() {
        TemperateZone zone = new TemperateZone();
        assertFalse(zone.containsPlayerX(19));
        assertFalse(zone.containsPlayerX(40));
    }

    // ── HumidZone: containsPlayerX ─────────────────────────────────────────────

    /** Verifies that HumidZone contains columns 40 through 59 (inclusive). */
    @Test
    void humidZoneContainsFourtyToFiftyNine() {
        HumidZone zone = new HumidZone();
        assertTrue(zone.containsPlayerX(40));
        assertTrue(zone.containsPlayerX(50));
        assertTrue(zone.containsPlayerX(59));
    }

    /** Verifies that HumidZone rejects columns outside the 40–59 range. */
    @Test
    void humidZoneRejectsColumnsOutsideItsRange() {
        HumidZone zone = new HumidZone();
        assertFalse(zone.containsPlayerX(39));
        assertFalse(zone.containsPlayerX(60));
    }

    // ── AridZone: passive terrain effect ──────────────────────────────────────

    /** Verifies that AridZone evaporates a Puddle within radius and replaces it with Floor. */
    @Test
    void aridZoneEvaporatesPuddleIntoFloor() throws Exception {
        GameMap map = new TestWorld().createFloorMap(15, 15);
        Location player = map.at(7, 7);
        Location puddleTile = map.at(7, 8);
        puddleTile.setGround(new Puddle());

        new AridZone().applyPassiveEffect(map, player);

        assertNull(puddleTile.getGroundAs(PuddleGround.class),
                "Puddle within radius must be evaporated");
        assertNotNull(puddleTile.getGroundAs(Seedable.class),
                "Evaporated tile must become Floor (implements Seedable)");
    }

    /** Verifies that AridZone evaporates at most 2 Puddles when 3 are present. */
    @Test
    void aridZoneCapsPuddleEvaporationAtTwo() throws Exception {
        GameMap map = new TestWorld().createFloorMap(15, 15);
        Location player = map.at(7, 7);
        List<Location> puddles = Arrays.asList(map.at(7, 8), map.at(7, 9), map.at(7, 10));
        for (Location loc : puddles) { loc.setGround(new Puddle()); }

        new AridZone().applyPassiveEffect(map, player);

        int remaining = 0;
        for (Location loc : puddles) {
            if (loc.getGroundAs(PuddleGround.class) != null) remaining++;
        }
        assertEquals(1, remaining,
                "3 Puddles placed; cap is 2 evaporations so exactly 1 must survive");
    }

    /** Verifies that ToxicWaste tiles in the radius are diluted into either Puddle or remain ToxicWaste. */
    @Test
    void aridZoneToxicWasteDilutionProducesOnlyPuddleOrToxicWaste() throws Exception {
        GameMap map = new TestWorld().createFloorMap(15, 15);
        Location player = map.at(7, 7);
        List<Location> toxics = Arrays.asList(
                map.at(6, 7), map.at(8, 7), map.at(7, 6), map.at(7, 8), map.at(6, 8));
        for (Location loc : toxics) { loc.setGround(new ToxicWaste()); }

        new AridZone().applyPassiveEffect(map, player);

        for (Location loc : toxics) {
            boolean isPuddle = loc.getGroundAs(PuddleGround.class) != null;
            boolean isToxic  = loc.getGroundAs(ToxicGround.class)  != null;
            assertTrue(isPuddle || isToxic,
                    "ToxicWaste must only dilute into Puddle or remain ToxicWaste");
        }
    }

    // ── TemperateZone: passive terrain effect ─────────────────────────────────

    /** Verifies that TemperateZone advances a FleshySprout within radius to FleshySapling. */
    @Test
    void temperateZoneAdvancesFleshySproutToFleshySapling() throws Exception {
        GameMap map = new TestWorld().createFloorMap(15, 15);
        Location player = map.at(7, 7);
        Location sproutTile = map.at(7, 8);
        sproutTile.setGround(new FleshySprout());

        new TemperateZone().applyPassiveEffect(map, player);

        assertNull(sproutTile.getGroundAs(FleshySproutGround.class),
                "FleshySprout within radius must be advanced");
        assertNotNull(sproutTile.getGroundAs(FleshySaplingGround.class),
                "Advanced Sprout tile must become FleshySapling");
    }

    /** Verifies that TemperateZone advances at most 2 FleshySprouts when 3 are present. */
    @Test
    void temperateZoneCapsSproutAdvancementAtTwo() throws Exception {
        GameMap map = new TestWorld().createFloorMap(15, 15);
        Location player = map.at(7, 7);
        List<Location> sprouts = Arrays.asList(map.at(7, 8), map.at(7, 9), map.at(7, 10));
        for (Location loc : sprouts) { loc.setGround(new FleshySprout()); }

        new TemperateZone().applyPassiveEffect(map, player);

        int remaining = 0;
        for (Location loc : sprouts) {
            if (loc.getGroundAs(FleshySproutGround.class) != null) remaining++;
        }
        assertEquals(1, remaining,
                "3 Sprouts placed; cap is 2 advances so exactly 1 must remain");
    }

    /** Verifies that TemperateZone advances a FleshySapling within radius to FleshyMatureTree. */
    @Test
    void temperateZoneAdvancesFleshySaplingToFleshyMatureTree() throws Exception {
        GameMap map = new TestWorld().createFloorMap(15, 15);
        Location player = map.at(7, 7);
        Location saplingTile = map.at(7, 8);
        saplingTile.setGround(new FleshySapling());

        new TemperateZone().applyPassiveEffect(map, player);

        assertNull(saplingTile.getGroundAs(FleshySaplingGround.class),
                "FleshySapling within radius must be advanced");
        assertNotNull(saplingTile.getGroundAs(FleshyMatureTreeGround.class),
                "Advanced Sapling tile must become FleshyMatureTree");
    }

    /** Verifies that TemperateZone advances at most 1 FleshySapling when 2 are present. */
    @Test
    void temperateZoneCapsSaplingAdvancementAtOne() throws Exception {
        GameMap map = new TestWorld().createFloorMap(15, 15);
        Location player = map.at(7, 7);
        List<Location> saplings = Arrays.asList(map.at(7, 8), map.at(7, 9));
        for (Location loc : saplings) { loc.setGround(new FleshySapling()); }

        new TemperateZone().applyPassiveEffect(map, player);

        int remaining = 0;
        for (Location loc : saplings) {
            if (loc.getGroundAs(FleshySaplingGround.class) != null) remaining++;
        }
        assertEquals(1, remaining,
                "2 Saplings placed; cap is 1 advancement so exactly 1 must remain");
    }

    // ── HumidZone: passive terrain effect ─────────────────────────────────────

    /** Verifies that HumidZone extinguishes a Fire within radius and restores the previous ground. */
    @Test
    void humidZoneExtinguishesFireAndRestoresPreviousGround() throws Exception {
        GameMap map = new TestWorld().createFloorMap(15, 15);
        Location player = map.at(7, 7);
        Location fireTile = map.at(7, 8);
        fireTile.setGround(new Fire(new Floor()));

        new HumidZone().applyPassiveEffect(map, player);

        assertNull(fireTile.getGroundAs(FireHazard.class),
                "Fire within radius must be extinguished");
        assertNotNull(fireTile.getGroundAs(Seedable.class),
                "Extinguished tile must restore its previous ground (Floor implements Seedable)");
    }

    /** Verifies that HumidZone extinguishes at most 2 fires when 3 are present. */
    @Test
    void humidZoneCapsFireExtinguishedAtTwo() throws Exception {
        GameMap map = new TestWorld().createFloorMap(15, 15);
        Location player = map.at(7, 7);
        List<Location> fires = Arrays.asList(map.at(7, 8), map.at(7, 9), map.at(7, 10));
        for (Location loc : fires) { loc.setGround(new Fire(new Floor())); }

        new HumidZone().applyPassiveEffect(map, player);

        int remaining = 0;
        for (Location loc : fires) {
            if (loc.getGroundAs(FireHazard.class) != null) remaining++;
        }
        assertEquals(1, remaining,
                "3 fires placed; cap is 2 extinguished so exactly 1 must remain");
    }

    /** Verifies that HumidZone calls forceSpread on FungalGround tiles within the radius. */
    @Test
    void humidZoneForceSpreadsFungalGroundTileInRadius() throws Exception {
        GameMap map = new TestWorld().createFloorMap(15, 15);
        Location player = map.at(7, 7);
        TestFungalGround fungal = new TestFungalGround();
        map.at(7, 8).setGround(fungal);

        new HumidZone().applyPassiveEffect(map, player);

        assertTrue(fungal.spreadCalled,
                "HumidZone must call forceSpread() on FungalGround tiles within radius");
    }
}
