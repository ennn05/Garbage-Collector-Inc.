package req1;

import edu.monash.fit2099.engine.positions.GameMap;
import game.economy.QuotaSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link QuotaSystem}: initial state, quota accumulation,
 * deadline detection, rank advancement, ceil-rounded scaling, and missed-deadline resets.
 */
class QuotaSystemTest {

    private QuotaSystem quota;
    private GameMap testMap;

    /** Initialises a 5×5 floor map and creates a QuotaSystem anchored at (2,2). */
    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(5, 5);
        quota = new QuotaSystem(testMap.at(2, 2));
    }

    // ── Initial state ──────────────────────────────────────────────────────────

    /** Verifies that the quota system starts at rank 1. */
    @Test
    void initialRankIsOne() {
        assertEquals(1, quota.getCurrentRank());
    }

    /** Verifies that the initial base quota is 100. */
    @Test
    void initialBaseQuotaIs100() {
        assertEquals(100, quota.getBaseQuota());
    }

    /** Verifies that the initial time limit is 200 turns. */
    @Test
    void initialTimeLimitIs200() {
        assertEquals(200, quota.getTimeLimit());
    }

    /** Verifies that quota progress starts at zero. */
    @Test
    void initialProgressIsZero() {
        assertEquals(0, quota.getCurrentQuotaProgress());
    }

    /** Verifies that the turn counter starts at zero. */
    @Test
    void initialTurnIsZero() {
        assertEquals(0, quota.getCurrentTurn());
    }

    /** Verifies that the quota is not met at creation (progress = 0, base = 100). */
    @Test
    void initialQuotaNotMet() {
        assertFalse(quota.isQuotaMet());
    }

    // ── progressQuota ──────────────────────────────────────────────────────────

    /** Verifies that multiple calls to {@code progressQuota} accumulate correctly. */
    @Test
    void progressQuotaAccumulatesCorrectly() {
        quota.progressQuota(30);
        quota.progressQuota(20);
        assertEquals(50, quota.getCurrentQuotaProgress());
    }

    /** Verifies that the quota is met once progress reaches the base quota. */
    @Test
    void quotaMetOnceProgressReachesBase() {
        quota.progressQuota(100);
        assertTrue(quota.isQuotaMet());
    }

    /** Verifies that progress one below the base quota does not satisfy the quota. */
    @Test
    void quotaNotMetBelowBase() {
        quota.progressQuota(99);
        assertFalse(quota.isQuotaMet());
    }

    /** Verifies that exceeding the base quota also satisfies the quota condition. */
    @Test
    void quotaMetExceedingBase() {
        quota.progressQuota(150);
        assertTrue(quota.isQuotaMet());
    }

    // ── isDeadlineReached ──────────────────────────────────────────────────────

    /** Verifies that the deadline is not reached before the full time limit elapses. */
    @Test
    void deadlineNotReachedBeforeTimeLimit() throws Exception {
        for (int i = 0; i < 199; i++) {
            quota.tick(testMap);
        }
        assertFalse(quota.isDeadlineReached());
    }

    /** Verifies that the turn counter resets to zero after the deadline is processed. */
    @Test
    void deadlineReachedAtTimeLimit() throws Exception {
        for (int i = 0; i < 200; i++) {
            quota.tick(testMap);
        }
        // After tick processes deadline, currentTurn resets to 0
        // so we check it triggered by observing the reset
        assertEquals(0, quota.getCurrentTurn());
    }

    // ── Rank advancement ───────────────────────────────────────────────────────

    /** Verifies that meeting the quota before the deadline advances the rank by 1. */
    @Test
    void rankAdvancesWhenQuotaMetAtDeadline() throws Exception {
        quota.progressQuota(100);
        for (int i = 0; i < 200; i++) {
            quota.tick(testMap);
        }
        assertEquals(2, quota.getCurrentRank());
    }

    /** Verifies that quota progress resets to zero after a successful rank advancement. */
    @Test
    void progressResetsAfterRankAdvancement() throws Exception {
        quota.progressQuota(100);
        for (int i = 0; i < 200; i++) {
            quota.tick(testMap);
        }
        assertEquals(0, quota.getCurrentQuotaProgress());
    }

    /** Verifies that the turn counter resets to zero after a successful rank advancement. */
    @Test
    void turnResetsAfterRankAdvancement() throws Exception {
        quota.progressQuota(100);
        for (int i = 0; i < 200; i++) {
            quota.tick(testMap);
        }
        assertEquals(0, quota.getCurrentTurn());
    }

    // ── Quota/time scaling with ceil rounding ──────────────────────────────────

    /** Verifies that the base quota after rank 1 is {@code ceil(100 * 1.05) = 105}. */
    @Test
    void newBaseQuotaIs105AfterFirstRankUp() throws Exception {
        // ceil(100 * 1.05) = ceil(105.0) = 105
        quota.progressQuota(100);
        for (int i = 0; i < 200; i++) quota.tick(testMap);
        assertEquals(105, quota.getBaseQuota());
    }

    /** Verifies that the time limit after rank 1 is {@code 200 + ceil(200 * 0.10) = 220}. */
    @Test
    void newTimeLimitIs220AfterFirstRankUp() throws Exception {
        // 200 + ceil(200 * 0.10) = 200 + ceil(20.0) = 220
        quota.progressQuota(100);
        for (int i = 0; i < 200; i++) quota.tick(testMap);
        assertEquals(220, quota.getTimeLimit());
    }

    /** Verifies that ceil rounding is applied consistently when computing the quota increase at rank 2. */
    @Test
    void ceilRoundingAppliedOnQuotaIncrease() throws Exception {
        // After rank 1: base = 100 + ceil(100 * 0.05) = 105
        // After rank 2: base = 105 + ceil(105 * 0.05) = 105 + ceil(5.25) = 111
        quota.progressQuota(100);
        for (int i = 0; i < 200; i++) quota.tick(testMap);
        int limit2 = quota.getTimeLimit(); // 220
        quota.progressQuota(105);
        for (int i = 0; i < limit2; i++) quota.tick(testMap);
        assertEquals(111, quota.getBaseQuota());
    }

    /** Verifies that ceil rounding is applied consistently when computing the time limit increase at rank 2. */
    @Test
    void ceilRoundingAppliedOnTimeLimitIncrease() throws Exception {
        // After rank 1: limit = 220
        // After rank 2: limit = 220 + ceil(220 * 0.10) = 220 + ceil(22.0) = 242
        quota.progressQuota(100);
        for (int i = 0; i < 200; i++) quota.tick(testMap);
        int limit2 = quota.getTimeLimit(); // 220
        quota.progressQuota(105);
        for (int i = 0; i < limit2; i++) quota.tick(testMap);
        assertEquals(242, quota.getTimeLimit());
    }

    // ── Deadline missed: reset without rank change ─────────────────────────────

    /** Verifies that missing the deadline (no progress) does not advance the rank. */
    @Test
    void rankDoesNotAdvanceWhenQuotaMissed() throws Exception {
        // Don't deposit anything
        for (int i = 0; i < 200; i++) quota.tick(testMap);
        assertEquals(1, quota.getCurrentRank());
    }

    /** Verifies that the turn counter resets to zero even when the deadline is missed. */
    @Test
    void turnResetsAfterMissedDeadline() throws Exception {
        for (int i = 0; i < 200; i++) quota.tick(testMap);
        assertEquals(0, quota.getCurrentTurn());
    }

    /** Verifies that quota progress resets to zero even when the deadline is missed. */
    @Test
    void progressResetsAfterMissedDeadline() throws Exception {
        quota.progressQuota(50);
        for (int i = 0; i < 200; i++) quota.tick(testMap);
        assertEquals(0, quota.getCurrentQuotaProgress());
    }
}
