package game;

/**
 * Enum representing security clearance levels for door access.
 * Higher levels can open doors with lower clearance requirements.
 */
public enum ClearanceLevel {
    LEVEL_1,
    LEVEL_2,
    LEVEL_3;

    /**
     * Check if this clearance level can open a door with the required clearance.
     * Higher levels can always open lower level doors.
     *
     * @param required the clearance level required by the door
     * @return true if this level can open the door, false otherwise
     */
    public boolean canOpen(ClearanceLevel required) {
        // LEVEL_3 can open LEVEL_1, LEVEL_2, LEVEL_3
        // LEVEL_2 can open LEVEL_1, LEVEL_2
        // LEVEL_1 can open LEVEL_1 only
        return this.ordinal() >= required.ordinal();
    }
}
