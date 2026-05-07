package game.enums;

/**
 * Represents the clearance level of an access card.
 */
public enum AccessLevel {
    LEVEL_1("Level 1", '▤', 1),
    LEVEL_2("Level 2", 'α', 2),
    LEVEL_3("Level 3", '◐', 3);

    private final String label;
    private final char displayChar;
    private final int weight;

    AccessLevel(String label, char displayChar, int weight) {
        this.label = label;
        this.displayChar = displayChar;
        this.weight = weight;
    }

    /**
     * Get the readable label of this access level.
     *
     * @return access level label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get the display character used by a card of this level.
     *
     * @return display character
     */
    public char getDisplayChar() {
        return displayChar;
    }

    /**
     * Get the weight of a card of this level.
     *
     * @return card weight
     */
    public int getWeight() {
        return weight;
    }
}