package game.states;

/**
 * Holds one singleton instance of each Mannequin state.
 * States use this to return references to the next state during transitions.
 */
public class StateManager {
    private final MannequinState idle;
    private final MannequinState active;
    private final MannequinState berserk;
    private final MannequinState mimic;

    public StateManager() {
        this.idle = new IdleState();
        this.active = new ActiveState();
        this.berserk = new BerserkState();
        this.mimic = new MimicState();
    }

    public MannequinState idle() {
        return idle;
    }

    public MannequinState active() {
        return active;
    }

    public MannequinState berserk() {
        return berserk;
    }

    public MannequinState mimic() {
        return mimic;
    }
}
