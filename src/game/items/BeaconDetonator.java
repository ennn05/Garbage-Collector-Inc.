package game.items;

import edu.monash.fit2099.engine.items.Item;

public class BeaconDetonator extends Item {
    private static final int DETONATE_RANGE = 5;

    public BeaconDetonator() {
        super("Remote Detonator", '[');
        this.makePortable();
    }

    public static int getDetonateRange() {
        return DETONATE_RANGE;
    }
}
