package game.interfaces;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;

public interface Resonator {
    void triggerShockwave(Location epicenter, GameMap map);
    int getShockwaveRadius();
    int getShockwavePower();

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

    default boolean isValidItemTarget(Location location) {
        if (location == null) return false;
        return !location.getGround().blocksThrownObjects();
    }

    default int sign(int v) {
        return Integer.compare(v, 0);
    }
}
