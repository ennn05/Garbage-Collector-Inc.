package game.economy;

/**
 * Stores the credits owned by a worker.
 * A wallet cannot store more than 1000 credits.
 */
public class Wallet {
    public static final int MAX_CREDITS = 1000;

    private int credits;

    /**
     * Create an empty wallet.
     */
    public Wallet() {
        this.credits = 0;
    }

    /**
     * Get the current number of credits.
     *
     * @return current credits
     */
    public int getCredits() {
        return credits;
    }

    /**
     * Check whether the wallet has enough credits.
     *
     * @param amount required amount
     * @return true if the wallet has at least this amount
     */
    public boolean canAfford(int amount) {
        return credits >= amount;
    }

    /**
     * Add credits without exceeding the maximum wallet capacity.
     *
     * @param amount credits to add
     * @return actual credits added
     */
    public int addCredits(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot add a negative amount of credits.");
        }

        int oldCredits = credits;
        credits = Math.min(MAX_CREDITS, credits + amount);
        return credits - oldCredits;
    }

    /**
     * Deduct credits without allowing the wallet to go below zero.
     *
     * @param amount credits to deduct
     * @return actual credits deducted
     */
    public int deductCredits(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot deduct a negative amount of credits.");
        }

        int oldCredits = credits;
        credits = Math.max(0, credits - amount);
        return oldCredits - credits;
    }
}