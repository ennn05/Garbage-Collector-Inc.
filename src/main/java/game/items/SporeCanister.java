package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.actions.EmitSporesAction;
import game.economy.Wallet;
import game.enums.ItemStatistics;
import game.grounds.BlightFungus;
import game.grounds.FungalGround;
import game.grounds.SporeColony;
import game.grounds.SporeExplosion;
import game.interfaces.Seedable;
import game.interfaces.Sellable;
import game.interfaces.SporeEmitter;


/**
 * A pressurised canister of fungal spores obtainable only by cutting a SporeColony.
 *
 * - Can be sold to the Supercomputer for 60 credits.
 * - When deployed via EmitSporesAction: seeds BlightFungus on the target Floor tile.
 *   25% chance of an AoE burst that also converts all adjacent Floor tiles.
 */
public class SporeCanister extends Item implements SporeEmitter, Sellable {

    private static final int WEIGHT = 3;
    private static final int SELL_PRICE = 60;

    /**
     * Creates a SporeCanister (display character: {@code ¡}).
     * The canister weighs {@value #WEIGHT} units and can be carried in the actor's inventory.
     */
    public SporeCanister() {
        super("SporeCanister", '¡');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    /**
     * Generates one {@link EmitSporesAction} per exit that leads to a
     * {@link game.interfaces.Seedable} tile, allowing the holder to deploy any of the
     * three supported fungal types (BlightFungus, SporeExplosion, SporeColony).
     *
     * @param actor    the actor holding this canister
     * @param location the actor's current location
     * @return the list of deployment actions available this turn
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location) {
        ActionList actions = super.allowableActions(actor, location);
        for (Exit exit : location.getExits()) {
            Location target = exit.getDestination();
            if (target.getGroundAs(Seedable.class) != null) {
                actions.add(new EmitSporesAction(BlightFungus::new,   "BlightFungus",   target, exit.getName()));
                actions.add(new EmitSporesAction(SporeExplosion::new, "SporeExplosion", target, exit.getName()));
                actions.add(new EmitSporesAction(SporeColony::new,    "SporeColony",    target, exit.getName()));
            }
        }
        return actions;
    }

    /**
     * Seeds a {@link game.grounds.BlightFungus} directly onto the given location if
     * it is currently {@link game.interfaces.Seedable}. Used when the actor is
     * infected with {@link game.status.SporeInfection} and spreads spores passively.
     *
     * @param source the location to seed
     */
    @Override
    public void emitSpores(Location source) {
        if (source.getGroundAs(Seedable.class) != null) {
            source.setGround(new BlightFungus());
            System.out.println("SporeCanister seeds BlightFungus at " + source + "!");
        }
    }

    /**
     * Returns the primary spore type this canister contains.
     *
     * @return {@link BlightFungus}{@code .class}
     */
    @Override
    public Class<? extends FungalGround> getSporeType() {
        return BlightFungus.class;
    }

    /**
     * Returns the sell price for this canister at the Supercomputer terminal.
     *
     * @return {@value #SELL_PRICE} credits
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Handles the transaction when the canister is sold to the Supercomputer.
     *
     * @param seller           the actor selling the item
     * @param map              the game map
     * @param terminalLocation the location of the Supercomputer
     * @param wallet           the seller's wallet
     * @return a message confirming the sale
     */
    @Override
    public String onSold(Actor seller, GameMap map, Location terminalLocation, Wallet wallet) {
        return seller + " cashes in the SporeCanister for " + SELL_PRICE + " credits.";
    }
}
