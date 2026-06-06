package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.ResonanceMallet;

public class MalletAction extends Action {
    private final Location target;
    private final String direction;
    private final ResonanceMallet mallet;

    public MalletAction(Location target, String direction, ResonanceMallet mallet) {
        this.target = target;
        this.direction = direction;
        this.mallet = mallet;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        mallet.triggerShockwave(target, map);
        mallet.applyCooldown();
        return actor + " bonked at " + direction;
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " bonks at " + direction;
    }
}
