package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.interfaces.SporeEmitter;

/**
 * Action created by a SporeCanister that lets the worker deploy fungal spores
 * onto a chosen adjacent Floor tile.
 */
public class EmitSporesAction extends Action {

    private final SporeEmitter emitter;
    private final Location target;
    private final String direction;

    public EmitSporesAction(SporeEmitter emitter, Location target, String direction) {
        this.emitter = emitter;
        this.target = target;
        this.direction = direction;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        emitter.emitSpores(target);
        return actor + " deploys the SporeCanister to the " + direction + "!";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " deploys SporeCanister to the " + direction;
    }
}
