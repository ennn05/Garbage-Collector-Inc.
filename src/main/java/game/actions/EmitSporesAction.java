package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.FungalGround;
import game.interfaces.Seedable;

import java.util.Random;
import java.util.function.Supplier;

/**
 * Action created by a SporeCanister that lets the worker deploy a chosen fungal
 * ground type onto a target adjacent Floor tile, with a 25% chance of an AoE
 * burst that seeds the same type on all surrounding Floor tiles simultaneously.
 */
public class EmitSporesAction extends Action {

    private final Supplier<FungalGround> groundFactory;
    private final String groundName;
    private final Location target;
    private final String direction;

    private static final int AOE_CHANCE = 25;
    private static final Random random = new Random();

    /**
     * Creates an action that will seed one specific fungal ground type onto a target tile.
     *
     * @param groundFactory supplier that creates the chosen {@link FungalGround} subtype
     * @param groundName    display name shown in the action menu
     * @param target        the {@link game.interfaces.Seedable} tile to seed
     * @param direction     direction label used in the menu description
     */
    public EmitSporesAction(Supplier<FungalGround> groundFactory, String groundName,
                            Location target, String direction) {
        this.groundFactory = groundFactory;
        this.groundName = groundName;
        this.target = target;
        this.direction = direction;
    }

    /**
     * Seeds the target tile with the chosen fungal ground type. If the target is
     * no longer {@link game.interfaces.Seedable}, the action is aborted. On a
     * {@value #AOE_CHANCE}% chance, all adjacent Seedable tiles are also seeded.
     *
     * @param actor the actor deploying the canister
     * @param map   the current game map
     * @return a string describing what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (target.getGroundAs(Seedable.class) == null) {
            return actor + " cannot seed fungus here — the tile is not a bare floor.";
        }

        target.setGround(groundFactory.get());
        System.out.println("A " + groundName + " takes root to the " + direction + "!");

        if (random.nextInt(100) < AOE_CHANCE) {
            System.out.println("AoE burst! " + groundName + " erupts to all surrounding tiles!");
            for (Exit exit : target.getExits()) {
                Location adj = exit.getDestination();
                if (adj.getGroundAs(Seedable.class) != null) {
                    adj.setGround(groundFactory.get());
                }
            }
        }

        return actor + " deploys SporeCanister [" + groundName + "] to the " + direction + "!";
    }

    /**
     * Returns the menu label shown to the player when choosing this action.
     *
     * @param actor the actor holding the canister
     * @return a human-readable description including the ground type and direction
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " deploys SporeCanister [" + groundName + "] to the " + direction;
    }
}
