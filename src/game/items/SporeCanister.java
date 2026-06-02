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
import game.interfaces.Seedable;
import game.interfaces.Sellable;
import game.interfaces.SporeEmitter;

import java.util.Random;

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
    private static final int AOE_CHANCE = 25;

    private final Random random = new Random();

    public SporeCanister() {
        super("SporeCanister", '¡');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location) {
        ActionList actions = super.allowableActions(actor, location);
        for (Exit exit : location.getExits()) {
            Location target = exit.getDestination();
            if (target.getGroundAs(Seedable.class) != null) {
                actions.add(new EmitSporesAction(this, target, exit.getName()));
            }
        }
        return actions;
    }

    @Override
    public void emitSpores(Location source) {
        if (source.getGroundAs(Seedable.class) == null) {
            return;
        }

        source.setGround(new BlightFungus());
        System.out.println("A BlightFungus takes root at " + source + "!");

        if (random.nextInt(100) < AOE_CHANCE) {
            System.out.println("SporeCanister AoE burst! Blight spreads to surrounding tiles!");
            for (Exit exit : source.getExits()) {
                Location adj = exit.getDestination();
                if (adj.getGroundAs(Seedable.class) != null) {
                    adj.setGround(new BlightFungus());
                }
            }
        }
    }

    @Override
    public Class<? extends FungalGround> getSporeType() {
        return BlightFungus.class;
    }

    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    @Override
    public String onSold(Actor seller, GameMap map, Location terminalLocation, Wallet wallet) {
        return seller + " cashes in the SporeCanister for " + SELL_PRICE + " credits.";
    }
}
