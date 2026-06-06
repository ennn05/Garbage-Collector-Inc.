package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

public abstract class CrackableGround extends Ground {
    private final int degradationLevel;

    protected CrackableGround(char displayChar, String name, int initialDegradationLevel) {
        super(displayChar, name);
        this.degradationLevel = initialDegradationLevel;
    }

    public abstract void onActorStep(Location location, Actor actor);

    public int getDegradationLevel() {
        return this.degradationLevel;
    }

    public abstract Ground degrade();
}
