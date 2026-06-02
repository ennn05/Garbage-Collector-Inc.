package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import game.actors.Slime;
import game.economy.Wallet;
import game.enums.ItemStatistics;
import game.interfaces.Depositable;
import game.interfaces.Purchasable;
import game.interfaces.Sellable;

import java.util.Random;

/**
 * Industrial fan dropped from cutting vents.
 * Can be sold for 150 worker credits or deposited for 10 company credits with healing.
 */
public class IndustrialFan extends Item implements Purchasable, Sellable, Depositable {
    private static final int WEIGHT = 5;
    private static final int SELL_PRICE = 150;
    private static final int DEPOSIT_REWARD = 10;
    private static final int HEAL_AMOUNT = 10;
    private static final int PURCHASE_PRICE = 0;  // Not meant to be purchased, but implements Purchasable

    private final Random random = new Random();

    /**
     * Constructor for IndustrialFan.
     */
    public IndustrialFan() {
        super("Industrial Fan", '@');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
    }

    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE;
    }

    @Override
    public Item createPurchasedItem() {
        return new IndustrialFan();
    }

    @Override
    public String onPurchased(Actor buyer, GameMap map, Location terminalLocation, Wallet wallet) {
        return "";  // Not typically purchased
    }

    @Override
    public String onInsufficientCredits(Actor buyer, GameMap map) {
        return "";  // Not typically purchased
    }

    /**
     * Get the sell price for the industrial fan.
     *
     * @return sell price (150 worker credits)
     */
    @Override
    public int getSellPrice() {
        return SELL_PRICE;
    }

    /**
     * Handle selling the industrial fan.
     * Spawns a slime on an adjacent empty tile to the supercomputer.
     *
     * @param seller the actor selling this item
     * @param map the map where the transaction happens
     * @param terminalLocation the location of the Supercomputer
     * @param wallet the seller's wallet
     * @return result description
     */
    @Override
    public String onSold(Actor seller, GameMap map, Location terminalLocation, Wallet wallet) {
        String message = seller + " sells the Industrial Fan for " + SELL_PRICE + " worker credits. " +
                "Stripping the facility's cooling system triggers a hazard: a Slime spawns nearby!";
        
        // Try to spawn a slime on an adjacent empty location
        for (Exit exit : terminalLocation.getExits()) {
            Location adjacent = exit.getDestination();
            if (!adjacent.containsAnActor() && adjacent.canActorEnter(new Slime())) {
                try {
                    map.addActor(new Slime(), adjacent);
                    message += "\n[Slime spawned at " + adjacent + "]";
                    break;  // Only spawn one slime
                } catch (Exception e) {
                    // If spawning fails, continue
                }
            }
        }
        
        System.out.println(message);
        return message;
    }

    /**
     * Check if the actor can deposit this item.
     *
     * @param actor the actor attempting to deposit
     * @return true (always can be deposited)
     */
    @Override
    public boolean canBeDeposited(Actor actor) {
        return true;
    }

    /**
     * Handle depositing the industrial fan.
     * Heals the worker for 10 HP.
     *
     * @param actor the actor performing the deposit
     * @param location the location of the supercomputer
     * @return result description of the deposit
     */
    @Override
    public String onDeposit(Actor actor, Location location) {
        actor.modifyStatistic(ActorStatistics.HEALTH, StatisticOperations.INCREASE, HEAL_AMOUNT);
        String message = actor + " deposits the Industrial Fan for " + DEPOSIT_REWARD + 
                " company credits. The Company rewards compliance with fresh oxygen: " + 
                actor + " is healed for " + HEAL_AMOUNT + " HP!";
        System.out.println(message);
        return message;
    }

    /**
     * Get the company credits reward for depositing.
     *
     * @return deposit reward (10 company credits)
     */
    @Override
    public int getDepositReward() {
        return DEPOSIT_REWARD;
    }
}
