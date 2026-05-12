package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ConsumeFlaskAction;
import game.actions.UnlockDoorAction;
import game.economy.Wallet;
import game.economy.WalletHolder;
import game.enums.Ability;
import game.interfaces.*;
import game.status.HiveStatus;
import game.world.FacilityAlarmSystem;

/**
 * This brave soul is capable of performing complex tasks such as picking up trash
 * off the floor, swiping plastic cards at stubborn doors, and drinking mystery
 * fluids to stay alive.
 */
public class ContractedWorker extends Actor implements WalletHolder, Host {
    private static final int HIVE_SPAWN_INTERVAL = 4;
    private static final int HOST_DAMAGE = 1;

    private final Wallet wallet;

    public ContractedWorker(String name, char displayChar, int hitPoints, Inventory inventory) {
        super(name, displayChar, hitPoints, inventory);
        this.enableAbility(Ability.WORKER);
        this.wallet = new Wallet();
    }

    /**
     * Get the wallet owned by this worker.
     *
     * @return the worker's wallet
     */
    @Override
    public Wallet getWallet() {
        return wallet;
    }

    /**
     * Check whether a door unlocker can unlock a nearby unlockable ground.
     *
     * @param doorUnlocker the item that can unlock doors
     * @param map the map containing the actor
     * @return true if there is a nearby locked unlockable ground that can be unlocked
     */
    private boolean canUnlockNearbyDoor(DoorUnlocker doorUnlocker, GameMap map) {
        Location location = map.locationOf(this);

        for (Exit exit : location.getExits()) {
            Location surroundingLocation = exit.getDestination();
            Unlockable unlockable = surroundingLocation.getGroundAs(Unlockable.class);

            if (unlockable != null && doorUnlocker.canUnlock(unlockable)) {
                return true;
            }
        }

        return false;
    }

    /**
     * The playTurn method checks whether the current actor is unconscious due to environmental hazards.
     * Next, it checks whether the actor is carrying an item that can unlock nearby unlockable grounds.
     * If a drinkable item is available in the inventory, the actor will be able to drink from it.
     * Additionally, it handles multi-turn actions by getting the subsequent action returned by the previous action.
     * Finally, it adds all possible actions that the actor can perform in the current turn and shows them on the
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

        FacilityAlarmSystem alarmSystem = FacilityAlarmSystem.forMap(map);

        if (alarmSystem == null || alarmSystem.canDoorsBeUnlocked()) {
            for (DoorUnlocker doorUnlocker : this.getInventory().getItemsAs(DoorUnlocker.class)) {
                if (canUnlockNearbyDoor(doorUnlocker, map)) {
                    actions.add(new UnlockDoorAction(doorUnlocker));
                    break;
                }
            }
        }

        for (Drinkable drinkable : this.getInventory().getItemsAs(Drinkable.class)) {
            if (drinkable.canDrink()) {
                actions.add(new ConsumeFlaskAction(drinkable));
                break;
            }
        }

        if (lastAction.getNextAction() != null) {
            return lastAction.getNextAction();
        }

        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }

    @Override
    public String infect(Actor otherActor, GameMap gameMap) {
        Spawner spawner = (Spawner) otherActor;
        return otherActor + " infected " + this + "\n" + this.hive(spawner);
    }

    @Override
    public String hive(Spawner spawner) {
        this.addStatus(new HiveStatus(spawner, HIVE_SPAWN_INTERVAL));
        return this + " becomes a living hive.";
    }

    @Override
    public void hiveEffect() {
        this.hurt(HOST_DAMAGE);
    }
}
