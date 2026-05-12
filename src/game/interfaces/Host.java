package game.interfaces;

/**
 * Represents an entity that becomes a hive once infected by another actor.
 */
public interface Host extends Infectable {
    /**
     * Adds a status that turns this entity into a living hive.
     *
     * @param spawner the spawner that will be spawned by the hive.
     * @return a String describing the result of turning this actor into
     * a living hive
     */
    String hive(Spawner spawner);

    /**
     * Performs the side effect of spawning as a result of being a living hive.
     */
    void hiveEffect();
}
