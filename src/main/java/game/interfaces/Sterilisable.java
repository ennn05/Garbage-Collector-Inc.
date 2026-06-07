package game.interfaces;

/**
 * Represents something that can be sterilised.
 */
public interface Sterilisable {

    /**
     * Check whether the object has already been sterilised.
     *
     * @return true if already sterilised, false otherwise
     */
    boolean isSterilised();

    /**
     * Sterilise the object.
     */
    void sterilise();
}