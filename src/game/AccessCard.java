package game;

import edu.monash.fit2099.engine.items.Item;

/**
 * A class representing a small rectangular piece of plastic that holds entirely
 * too much power over your ability to walk through doors.
 * Its primary function is to beep happily when the player has clearance, and beep
 * angrily when they don't.
 * Essential for progressing the plot,
 *
 * @author Adrian Kristanto
 */
public class AccessCard extends Item {
    private ClearanceLevel clearanceLevel;

    /**
     * Constructor for AccessCard with a specified clearance level.
     * @param clearanceLevel the clearance level this card grants
     */
    public AccessCard(ClearanceLevel clearanceLevel) {
        super("Access Card", '▤');
        this.clearanceLevel = clearanceLevel;
    }

    /**
     * Default constructor with LEVEL_1 clearance.
     */
    public AccessCard() {
        this(ClearanceLevel.LEVEL_1);
    }

    /**
     * Get the clearance level of this access card.
     * @return the clearance level
     */
    public ClearanceLevel getClearanceLevel() {
        return clearanceLevel;
    }
}
