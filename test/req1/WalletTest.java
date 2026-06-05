package req1;

import game.economy.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    private Wallet wallet;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
    }

    @Test
    void initialBalanceIsZero() {
        assertEquals(0, wallet.getCredits());
    }

    @Test
    void addCreditsIncreasesBalance() {
        wallet.addCredits(100);
        assertEquals(100, wallet.getCredits());
    }

    @Test
    void addCreditsReturnsAmountActuallyAdded() {
        int added = wallet.addCredits(50);
        assertEquals(50, added);
    }

    @Test
    void addCreditsCapAtMaxCredits() {
        wallet.addCredits(900);
        int added = wallet.addCredits(200);   // would exceed 1000
        assertEquals(Wallet.MAX_CREDITS, wallet.getCredits());
        assertEquals(100, added);             // only 100 fits
    }

    @Test
    void addCreditsExactlyAtMax() {
        wallet.addCredits(Wallet.MAX_CREDITS);
        assertEquals(Wallet.MAX_CREDITS, wallet.getCredits());
    }

    @Test
    void addCreditsBeyondMaxDoesNotExceedMax() {
        wallet.addCredits(Wallet.MAX_CREDITS + 500);
        assertEquals(Wallet.MAX_CREDITS, wallet.getCredits());
    }

    @Test
    void addNegativeCreditsThrows() {
        assertThrows(IllegalArgumentException.class, () -> wallet.addCredits(-1));
    }

    @Test
    void deductCreditsDecreasesBalance() {
        wallet.addCredits(300);
        wallet.deductCredits(100);
        assertEquals(200, wallet.getCredits());
    }

    @Test
    void deductCreditsReturnsAmountActuallyDeducted() {
        wallet.addCredits(300);
        int deducted = wallet.deductCredits(100);
        assertEquals(100, deducted);
    }

    @Test
    void deductMoreThanBalanceFloorAtZero() {
        wallet.addCredits(50);
        int deducted = wallet.deductCredits(200);
        assertEquals(0, wallet.getCredits());
        assertEquals(50, deducted);           // only 50 was deducted
    }

    @Test
    void deductNegativeCreditsThrows() {
        assertThrows(IllegalArgumentException.class, () -> wallet.deductCredits(-1));
    }

    @Test
    void canAffordReturnsTrueWhenSufficientBalance() {
        wallet.addCredits(100);
        assertTrue(wallet.canAfford(100));
        assertTrue(wallet.canAfford(50));
    }

    @Test
    void canAffordReturnsFalseWhenInsufficientBalance() {
        wallet.addCredits(49);
        assertFalse(wallet.canAfford(50));
    }

    @Test
    void canAffordZeroAlwaysTrue() {
        assertTrue(wallet.canAfford(0));
    }
}

