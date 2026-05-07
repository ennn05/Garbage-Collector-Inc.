package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.SteriliseAction;
import game.enums.Ability;
import game.enums.ItemStatistics;
import game.interfaces.Sterilisable;

/**
 * A portable sterilisation box used to sterilise suitable targets.
 */
public class SterilisationBox extends Item {
    private static final int WEIGHT = 7;

    public SterilisationBox() {
        super("Sterilisation Box", '▣');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.enableAbility(Ability.STERILISING);
    }

    /**
     * Provide sterilise actions for sterilisable consumables that the actor is carrying,
     * items on the current location, and the ground on the current location.
     *
     * @param owner the actor carrying the item
     * @param map the map where the actor is
     * @return allowable actions for this item while being carried
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        Location currentLocation = map.locationOf(owner);

        for (Item item : owner.getInventory().getItems()) {
            item.asCapability(Sterilisable.class).ifPresent(sterilisable -> {
                if (!sterilisable.isSterilised()) {
                    actions.add(new SteriliseAction(sterilisable));
                }
            });
        }

        for (Item item : currentLocation.getItems()) {
            item.asCapability(Sterilisable.class).ifPresent(sterilisable -> {
                if (!sterilisable.isSterilised()) {
                    actions.add(new SteriliseAction(sterilisable));
                }
            });
        }

        Sterilisable groundTarget = currentLocation.getGroundAs(Sterilisable.class);
        if (groundTarget != null && !groundTarget.isSterilised()) {
            actions.add(new SteriliseAction(groundTarget));
        }

        return actions;
    }
}