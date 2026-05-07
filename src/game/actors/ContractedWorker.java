package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ConsumeFlaskAction;
import game.actions.UnlockDoorAction;
import game.enums.Ability;
import game.interfaces.Unlockable;
import game.items.AccessCard;
import game.items.Flask;
import game.world.FacilityAlarmSystem;
import edu.monash.fit2099.engine.positions.Location;

/**
 * This brave soul is capable of performing complex tasks such as picking up trash
 * off the floor, swiping plastic cards at stubborn doors, and drinking mystery
 * fluids to stay alive.
 */
public class ContractedWorker extends Actor {
    public ContractedWorker(String name, char displayChar, int hitPoints, Inventory inventory) {
        super(name, displayChar, hitPoints, inventory);
        this.enableAbility(Ability.WORKER);
    }

    /**
     * The playTurn method checks whether the current actor is unconscious due to environmental hazards.
     * Next, it will check if the player is carrying an access card. If so, they can open nearby unlockable grounds.
     * If the flask is available in the inventory, the player will be able to consume its content.
     * Additionally, it will also handle multi-turn actions by getting the subsequent action returned by the previous action.
     * Finally, it adds all possible actions that the actor can perform in the current turn and show it on the
     * console menu for the player to choose.
     *
     * @param actions collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn
     * @param map the map containing the Actor
     * @param display the I/O object to which messages may be written
     * @return the action that is chosen in the current turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (!this.isConscious()) {
            this.unconscious(map);
            return new DoNothingAction();
        }

        boolean isAccessCardAvailable = false;
        for (Item item : this.getInventory().getItems()) {
            if (item instanceof AccessCard) {
                isAccessCardAvailable = true;
                break;
            }
        }

        if (isAccessCardAvailable) {
            FacilityAlarmSystem alarmSystem = FacilityAlarmSystem.forMap(map);

            if (alarmSystem == null || alarmSystem.canDoorsBeUnlocked()) {
                Location location = map.locationOf(this);

                for (Exit exit : location.getExits()) {
                    Location surroundingLocation = exit.getDestination();
                    Unlockable unlockable = surroundingLocation.getGroundAs(Unlockable.class);

                    if (unlockable != null && !unlockable.isUnlocked()) {
                        actions.add(new UnlockDoorAction());
                        break;
                    }
                }
            }
        }

        boolean canConsumeFlask = false;
        for (Item item : this.getInventory().getItems()) {
            if (item instanceof Flask flask && flask.hasUsesRemaining()) {
                canConsumeFlask = true;
                break;
            }
        }

        if (canConsumeFlask) {
            actions.add(new ConsumeFlaskAction());
        }

        if (lastAction.getNextAction() != null) {
            return lastAction.getNextAction();
        }

        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }
}