package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.ResonanceMallet;

/**
 * Action that swings a {@link ResonanceMallet} at an adjacent tile, triggering its shockwave
 * and starting the mallet's cooldown.
 */
public class MalletAction extends Action {
    private final Location target;
    private final String direction;
    private final ResonanceMallet mallet;

    /**
     * Creates a MalletAction targeting a specific adjacent tile.
     *
     * @param target    the tile to strike
     * @param direction a human-readable direction label (e.g. "North")
     * @param mallet    the ResonanceMallet being swung
     */
    public MalletAction(Location target, String direction, ResonanceMallet mallet) {
        this.target = target;
        this.direction = direction;
        this.mallet = mallet;
    }

    /**
     * Triggers the mallet's shockwave at the target tile and applies the cooldown.
     *
     * @param actor the actor performing the swing
     * @param map   the current game map
     * @return a message describing the strike
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        mallet.triggerShockwave(target, map);
        mallet.applyCooldown();
        return actor + " bonked at " + direction;
    }

    /**
     * {@inheritDoc}
     *
     * @param actor the actor considering this action
     * @return a description of the swing direction for the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " bonks at " + direction;
    }
}
