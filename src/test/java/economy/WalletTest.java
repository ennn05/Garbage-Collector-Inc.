package economy;

import game.economy.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Wallet}: initial balance, credit addition (including cap),
 * credit deduction (including floor), and the {@code canAfford} predicate.
 */
class WalletTest {

    private Wallet wallet;

    /** Initialises a fresh Wallet with zero credits. */
    @BeforeEach
    void setUp() {
        wallet = new Wallet();
    }

    /** Verifies that a new Wallet starts with zero credits. */
    @Test
    void initialBalanceIsZero() {
        assertEquals(0, wallet.getCredits());
    }

    /** Verifies that {@code addCredits} increases the balance by the given amount. */
    @Test
    void addCreditsIncreasesBalance() {
        wallet.addCredits(100);
        assertEquals(100, wallet.getCredits());
    }

    /** Verifies that {@code addCredits} returns the amount actually added. */
    @Test
    void addCreditsReturnsAmountActuallyAdded() {
        int added = wallet.addCredits(50);
        assertEquals(50, added);
    }

    /** Verifies that the balance is capped at {@link Wallet#MAX_CREDITS} when credits would overflow. */
    @Test
    void addCreditsCapAtMaxCredits() {
        wallet.addCredits(900);
        int added = wallet.addCredits(200);   // would exceed 1000
        assertEquals(Wallet.MAX_CREDITS, wallet.getCredits());
        assertEquals(100, added);             // only 100 fits
    }

    /** Verifies that adding exactly MAX_CREDITS results in a balance equal to MAX_CREDITS. */
    @Test
    void addCreditsExactlyAtMax() {
        wallet.addCredits(Wallet.MAX_CREDITS);
        assertEquals(Wallet.MAX_CREDITS, wallet.getCredits());
    }

    /** Verifies that adding more than MAX_CREDITS in one call does not exceed the cap. */
    @Test
    void addCreditsBeyondMaxDoesNotExceedMax() {
        wallet.addCredits(Wallet.MAX_CREDITS + 500);
        assertEquals(Wallet.MAX_CREDITS, wallet.getCredits());
    }

    /** Verifies that passing a negative amount to {@code addCredits} throws an exception. */
    @Test
    void addNegativeCreditsThrows() {
        assertThrows(IllegalArgumentException.class, () -> wallet.addCredits(-1));
    }

    /** Verifies that {@code deductCredits} reduces the balance by the given amount. */
    @Test
    void deductCreditsDecreasesBalance() {
        wallet.addCredits(300);
        wallet.deductCredits(100);
        assertEquals(200, wallet.getCredits());
    }

    /** Verifies that {@code deductCredits} returns the amount actually deducted. */
    @Test
    void deductCreditsReturnsAmountActuallyDeducted() {
        wallet.addCredits(300);
        int deducted = wallet.deductCredits(100);
        assertEquals(100, deducted);
    }

    /** Verifies that deducting more than the balance floors the balance at zero and returns only what was available. */
    @Test
    void deductMoreThanBalanceFloorAtZero() {
        wallet.addCredits(50);
        int deducted = wallet.deductCredits(200);
        assertEquals(0, wallet.getCredits());
        assertEquals(50, deducted);           // only 50 was deducted
    }

    /** Verifies that passing a negative amount to {@code deductCredits} throws an exception. */
    @Test
    void deductNegativeCreditsThrows() {
        assertThrows(IllegalArgumentException.class, () -> wallet.deductCredits(-1));
    }

    /** Verifies that {@code canAfford} returns {@code true} when balance is sufficient. */
    @Test
    void canAffordReturnsTrueWhenSufficientBalance() {
        wallet.addCredits(100);
        assertTrue(wallet.canAfford(100));
        assertTrue(wallet.canAfford(50));
    }

    /** Verifies that {@code canAfford} returns {@code false} when balance is insufficient. */
    @Test
    void canAffordReturnsFalseWhenInsufficientBalance() {
        wallet.addCredits(49);
        assertFalse(wallet.canAfford(50));
    }

    /** Verifies that {@code canAfford(0)} always returns {@code true}. */
    @Test
    void canAffordZeroAlwaysTrue() {
        assertTrue(wallet.canAfford(0));
    }
}
