package game.interfaces;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

public interface Resonator {
    void triggerShockwave(Location epicenter, GameMap map);
    int getShockwaveRadius();
    int getShockwavePower();
}
