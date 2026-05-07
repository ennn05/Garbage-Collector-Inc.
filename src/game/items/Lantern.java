package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.BaseStatistic;
import game.enums.ItemStatistics;
import game.grounds.Fire;

import java.util.Random;

/**
 * An unstable lantern that may leak oil and ignite the ground while being carried.
 */
public class Lantern extends Item {
    private static final int WEIGHT = 7;
    private static final int INITIAL_FUEL = 10;
    private static final int LEAK_CHANCE_PERCENT = 5;

    private int oilFuel;
    private final Random random = new Random();

    public Lantern() {
        super("Lantern", '&');
        this.makePortable();
        this.addNewStatistic(ItemStatistics.WEIGHT, new BaseStatistic(WEIGHT));
        this.oilFuel = INITIAL_FUEL;
    }

    /**
     * Get the remaining oil fuel.
     *
     * @return remaining oil fuel
     */
    public int getOilFuel() {
        return oilFuel;
    }

    /**
     * While being carried, the lantern has a chance to leak and ignite the ground.
     *
     * @param currentLocation the actor's current location
     * @param actor the actor carrying the lantern
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        if (oilFuel <= 0) {
            return;
        }

        if (currentLocation.getGround() instanceof Fire) {
            return;
        }

        if (random.nextInt(100) < LEAK_CHANCE_PERCENT) {
            oilFuel--;
            currentLocation.setGround(new Fire(currentLocation.getGround()));
        }
    }
}