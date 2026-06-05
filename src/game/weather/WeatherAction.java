package game.weather;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * An engine Action that triggers one full weather update cycle.
 *
 * Created by {@link WeatherBehaviour} once per fetch interval and executed
 * automatically by the {@link game.actors.WeatherController} actor.  Delegating
 * the actual work to {@link WeatherSystem} keeps this class thin and ensures
 * the action itself has no knowledge of any concrete extractor or effect.
 */
public class WeatherAction extends Action {

    private final WeatherSystem weatherSystem;
    private final Location playerLocation;

    /**
     * @param weatherSystem  the system that fetches and applies weather data
     * @param playerLocation the active player's location, used to select the
     *                       API query zone and centre area-of-effect changes
     */
    public WeatherAction(WeatherSystem weatherSystem, Location playerLocation) {
        this.weatherSystem = weatherSystem;
        this.playerLocation = playerLocation;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        return weatherSystem.fetchAndApply(map, playerLocation);
    }

    @Override
    public String menuDescription(Actor actor) {
        return "Weather system update";
    }
}
