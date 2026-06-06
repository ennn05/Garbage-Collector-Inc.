package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

public class CrackedFloor extends CrackableGround {
    private static final int DEGRADATION_LEVEL = 1;
    private int gracePeriod = 1;

    public CrackedFloor() {
        super('Σ', "Cracked Floor", DEGRADATION_LEVEL);
    }

    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            onActorStep(location, location.getActor());
        }
    }

    @Override
    public void onActorStep(Location location, Actor actor) {
        if (this.gracePeriod <= 0) {
            RubbleField rubble = this.degrade();
            location.setGround(rubble);
            System.out.println(rubble.bury(actor, location.map()));
        }
        this.gracePeriod--;
    }

    @Override
    public RubbleField degrade() {
        return new RubbleField();
    }
}
