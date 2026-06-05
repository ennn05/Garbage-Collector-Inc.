package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

public abstract class CrackableGround extends Ground {
    private int degradationLevel;

    protected CrackableGround(char displayChar, String name) {
        super(displayChar, name);
    }

    public abstract void onActorStep(Location location, Actor actor);

    public int getDegradationLevel() {
        return this.degradationLevel;
    }

    public void applyShockwave(int power, Location location) {
        this.degradationLevel -= power;
    }
}
