package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.inventory.BasicInventory;
import game.weather.WeatherBehaviour;

/**
 * An invisible system actor that drives the weather update cycle each game turn.
 *
 * <p>The WeatherController is not a player, enemy, or interactable NPC. It sits
 * silently on an exterior dirt tile, invisible (display character ' '), and on
 * each of its turns delegates entirely to {@link WeatherBehaviour}. The behaviour
 * returns either a {@link game.weather.WeatherAction} (triggering an API call and
 * effect application) or a {@link DoNothingAction} depending on the turn counter.
 *
 * <p>This design keeps the weather pipeline fully contained within the
 * {@code game.weather} package: this class is deliberately thin, containing no
 * weather logic itself.
 */
public class WeatherController extends Actor {

    private final WeatherBehaviour weatherBehaviour;

    /**
     * @param weatherBehaviour the behaviour that schedules and executes weather cycles
     */
    public WeatherController(WeatherBehaviour weatherBehaviour) {
        super("Weather System", ' ', Integer.MAX_VALUE, new BasicInventory());
        this.weatherBehaviour = weatherBehaviour;
    }

    /**
     * Delegates entirely to {@link WeatherBehaviour#operate} each turn. Returns the
     * resulting {@link game.weather.WeatherAction} if the behaviour decides a weather
     * cycle should run, or {@link DoNothingAction} otherwise.
     *
     * @param actions    available actions (unused — weather logic is behaviour-driven)
     * @param lastAction the previous action taken (unused)
     * @param map        the current game map
     * @param display    the display (unused)
     * @return the action to execute this turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Action action = weatherBehaviour.operate(this, map.locationOf(this));
        return action != null ? action : new DoNothingAction();
    }
}
