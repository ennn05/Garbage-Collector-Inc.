package game.enums;

/**
 * A highly ambitious enumeration designed to categorize the myriad of complex,
 * nuanced statistics an item can possess in the game world.
 * Currently, it only tracks how hard gravity pulls on a thing. Future updates
 * may include groundbreaking concepts like "Value" or "Durability," but for now,
 * we just need to know if it will break the {@code ContractedWorker}'s back.
 */
public enum ItemStatistics {
    /** Tracks how many turns remain before an item can be used again. */
    COOLDOWN,
    /** Tracks the number of uses remaining before an item is consumed. */
    DURABILITY,
    /** The carry weight of an item, relevant to inventory capacity limits. */
    WEIGHT,
}
