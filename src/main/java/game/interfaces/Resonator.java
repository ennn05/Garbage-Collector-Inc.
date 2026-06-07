package game.interfaces;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Implemented by items that can trigger a shockwave on the game map.
 * <p>
 * A shockwave degrades {@link game.grounds.CrackableGround} tiles within its radius
 * and pushes items outward from the epicenter. Implementors define the radius and
 * power of their shockwave, and are responsible for the full {@link #triggerShockwave}
 * logic (including applying both effects).
 */
public interface Resonator {

    /**
     * Triggers a shockwave centred at the given epicenter, degrading crackable ground
     * and pushing items outward within the shockwave radius.
     *
     * @param epicenter the location at the centre of the shockwave
     * @param map       the game map on which the shockwave occurs
     */
    void triggerShockwave(Location epicenter, GameMap map);

    /**
     * Returns the radius (in tiles) of this item's shockwave.
     * A radius of 0 affects only the epicenter tile.
     *
     * @return the shockwave radius
     */
    int getShockwaveRadius();

    /**
     * Returns the power of this item's shockwave.
     * Currently unused in effect calculations but reserved for future scaling.
     *
     * @return the shockwave power
     */
    int getShockwavePower();

    /**
     * Finds a valid one-tile push destination for an item at {@code source}, pushed
     * away from {@code epicenter}. The preferred direction is directly away from the
     * epicenter; fallback directions are tried in priority order if the preferred tile
     * is blocked or out of bounds.
     * <p>
     * If {@code source} is at the epicenter (dx == dy == 0), the item is pushed east.
     *
     * @param source    the current location of the item to push
     * @param epicenter the shockwave origin used to determine push direction
     * @param map       the game map
     * @return the target location for the item, or {@code null} if no valid tile exists
     */
    default Location findOneTilePushTarget(Location source, Location epicenter, GameMap map) {
        int dx = source.x() - epicenter.x();
        int dy = source.y() - epicenter.y();

        int sx = sign(dx);
        int sy = sign(dy);
        if (sx == 0 && sy == 0) {
            sx = 1;
        }

        List<int[]> offsets = new ArrayList<>();
        offsets.add(new int[] {sx, sy});
        if (sx != 0) offsets.add(new int[] {sx, 0});
        if (sy != 0) offsets.add(new int[] {0, sy});

        for (int ox = -1; ox <= 1; ox++) {
            for (int oy = -1; oy <= 1; oy++) {
                if (ox == 0 && oy == 0) continue;
                boolean present = false;
                for (int[] e : offsets) {
                    if (e[0] == ox && e[1] == oy) {
                        present = true;
                        break;
                    }
                }
                if (!present) {
                    offsets.add(new int[] {ox, oy});
                }
            }
        }

        for (int[] off : offsets) {
            int tx = source.x() + off[0];
            int ty = source.y() + off[1];
            Location candidate;
            try {
                candidate = map.at(tx, ty);
            } catch (IndexOutOfBoundsException e) {
                continue;
            }
            if (candidate == null) continue;
            if (isValidItemTarget(candidate)) return candidate;
        }
        return null;
    }

    /**
     * Returns {@code true} if {@code location} can receive a pushed item —
     * that is, it exists and its ground does not block thrown objects.
     *
     * @param location the candidate target location
     * @return {@code true} if the location is a valid push destination
     */
    default boolean isValidItemTarget(Location location) {
        if (location == null) return false;
        return !location.getGround().blocksThrownObjects();
    }

    /**
     * Returns the sign of an integer: {@code -1}, {@code 0}, or {@code 1}.
     *
     * @param v the value to inspect
     * @return the signum of {@code v}
     */
    default int sign(int v) {
        return Integer.compare(v, 0);
    }
}
