package game.interfaces;

/**
 * Marker interface for {@link game.flora.FleshySapling} ground tiles.
 * Allows type-safe detection via {@code Location.getGroundAs(FleshySaplingGround.class)}
 * without using {@code instanceof}.
 */
public interface FleshySaplingGround {
}
