package game.interfaces;

/**
 * Marker interface for {@link game.grounds.Puddle} ground tiles.
 * Allows type-safe detection via {@code Location.getGroundAs(PuddleGround.class)}
 * without using {@code instanceof}.
 */
public interface PuddleGround {
}
