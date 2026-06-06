package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.CrackableGround;
import game.interfaces.Resonator;

import java.util.ArrayList;
import java.util.List;

public class TremorBeacon extends Item implements Resonator {
    private static final int SHOCKWAVE_POWER = 1;
    private static final int SHOCKWAVE_RADIUS = 2;

    public TremorBeacon() {
        super("Tremor Beacon", 'I');
        this.makePortable();
    }

    @Override
    public int getShockwavePower() {
        return SHOCKWAVE_POWER;
    }

    @Override
    public int getShockwaveRadius() {
        return SHOCKWAVE_RADIUS;
    }

    @Override
    public void triggerShockwave(Location epicenter, GameMap map) {
        List<Location> affected = epicenter.getNearbyLocations(getShockwaveRadius());
        for (Location location : affected) {
            if (!location.getItems().isEmpty()) {
                List<Item> items = new ArrayList<>(location.getItems());
                for (Item item : items) {
                    Location target = findOneTilePushTarget(location, epicenter, map);
                    if (target != null) {
                        location.removeItem(item);
                        target.addItem(item);
                    }
                }
            }
            CrackableGround ground = location.getGroundAs(CrackableGround.class);
            if (ground != null) ground.degrade();
        }
    }
}
