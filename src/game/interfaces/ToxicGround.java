package game.interfaces;

/**
 * Marker interface for {@link game.grounds.ToxicWaste} ground tiles.
 * Allows type-safe detection via {@code Location.getGroundAs(ToxicGround.class)}
 * without using {@code instanceof}.
 */
public interface ToxicGround {
}
