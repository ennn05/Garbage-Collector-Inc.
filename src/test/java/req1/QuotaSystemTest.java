package req1;

import edu.monash.fit2099.engine.positions.GameMap;
import game.economy.QuotaSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.TestWorld;

import static org.junit.jupiter.api.Assertions.*;

class QuotaSystemTest {

    private QuotaSystem quota;
    private GameMap testMap;

    @BeforeEach
    void setUp() throws Exception {
        testMap = new TestWorld().createFloorMap(5, 5);
        quota = new QuotaSystem(testMap.at(2, 2));
    }

    // â”€â”€ Initial state â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    void initialRankIsOne() {
        assertEquals(1, quota.getCurrentRank());
    }

    @Test
    void initialBaseQuotaIs100() {
        assertEquals(100, quota.getBaseQuota());
    }

    @Test
    void initialTimeLimitIs200() {
        assertEquals(200, quota.getTimeLimit());
    }

    @Test
    void initialProgressIsZero() {
        assertEquals(0, quota.getCurrentQuotaProgress());
    }

    @Test
    void initialTurnIsZero() {
        assertEquals(0, quota.getCurrentTurn());
    }

    @Test
    void initialQuotaNotMet() {
        assertFalse(quota.isQuotaMet());
    }

    // â”€â”€ progressQuota â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    void progressQuotaAccumulatesCorrectly() {
        quota.progressQuota(30);
        quota.progressQuota(20);
        assertEquals(50, quota.getCurrentQuotaProgress());
    }

    @Test
    void quotaMetOnceProgressReachesBase() {
        quota.progressQuota(100);
        assertTrue(quota.isQuotaMet());
    }

    @Test
    void quotaNotMetBelowBase() {
        quota.progressQuota(99);
        assertFalse(quota.isQuotaMet());
    }

    @Test
    void quotaMetExceedingBase() {
        quota.progressQuota(150);
        assertTrue(quota.isQuotaMet());
    }

    // â”€â”€ isDeadlineReached â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    void deadlineNotReachedBeforeTimeLimit() throws Exception {
        for (int i = 0; i < 199; i++) {
            quota.tick(testMap);
        }
        assertFalse(quota.isDeadlineReached());
    }

    @Test
    void deadlineReachedAtTimeLimit() throws Exception {
        for (int i = 0; i < 200; i++) {
            quota.tick(testMap);
        }
        // After tick processes deadline, currentTurn resets to 0
        // so we check it triggered by observing the reset
        assertEquals(0, quota.getCurrentTurn());
    }

    // â”€â”€ Rank advancement â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    void rankAdvancesWhenQuotaMetAtDeadline() throws Exception {
        quota.progressQuota(100);
        for (int i = 0; i < 200; i++) {
            quota.tick(testMap);
        }
        assertEquals(2, quota.getCurrentRank());
    }

    @Test
    void progressResetsAfterRankAdvancement() throws Exception {
        quota.progressQuota(100);
        for (int i = 0; i < 200; i++) {
            quota.tick(testMap);
        }
        assertEquals(0, quota.getCurrentQuotaProgress());
    }

    @Test
    void turnResetsAfterRankAdvancement() throws Exception {
        quota.progressQuota(100);
        for (int i = 0; i < 200; i++) {
            quota.tick(testMap);
        }
        assertEquals(0, quota.getCurrentTurn());
    }

    // â”€â”€ Quota/time scaling with ceil rounding â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    void newBaseQuotaIs105AfterFirstRankUp() throws Exception {
        // ceil(100 * 1.05) = ceil(105.0) = 105
        quota.progressQuota(100);
        for (int i = 0; i < 200; i++) quota.tick(testMap);
        assertEquals(105, quota.getBaseQuota());
    }

    @Test
    void newTimeLimitIs220AfterFirstRankUp() throws Exception {
        // 200 + ceil(200 * 0.10) = 200 + ceil(20.0) = 220
        quota.progressQuota(100);
        for (int i = 0; i < 200; i++) quota.tick(testMap);
        assertEquals(220, quota.getTimeLimit());
    }

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

    // â”€â”€ Deadline missed: reset without rank change â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    void rankDoesNotAdvanceWhenQuotaMissed() throws Exception {
        // Don't deposit anything
        for (int i = 0; i < 200; i++) quota.tick(testMap);
        assertEquals(1, quota.getCurrentRank());
    }

    @Test
    void turnResetsAfterMissedDeadline() throws Exception {
        for (int i = 0; i < 200; i++) quota.tick(testMap);
        assertEquals(0, quota.getCurrentTurn());
    }

    @Test
    void progressResetsAfterMissedDeadline() throws Exception {
        quota.progressQuota(50);
        for (int i = 0; i < 200; i++) quota.tick(testMap);
        assertEquals(0, quota.getCurrentQuotaProgress());
    }
}

