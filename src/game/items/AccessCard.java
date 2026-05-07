package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.enums.AccessLevel;
import game.enums.ItemStatistics;

/**
 * A portable access card with a security clearance level.
 */
public class AccessCard extends Item {
    private final AccessLevel level;

    /**
     * Create a level 1 access card by default.
     */
    public AccessCard() {
        this(AccessLevel.LEVEL_1);
    }

    /**
     * Create an access card with the given access level.
     *
     * @param level the clearance level of the card
     */
    public AccessCard(AccessLevel level) {
        super("Access Card " + level.getLabel(), level.getDisplayChar());
        this.level = level;
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(level.getWeight()));
    }

    /**
     * Get the clearance level of this card.
     *
     * @return the access level
     */
    public AccessLevel getLevel() {
        return level;
    }
}