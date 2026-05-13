package game.states;

/**
 * Mutable data shared across all Mannequin states for tracking cross-turn counters
 * and the current display character.
 */
public class StateData {
    private int idleTurnCount;
    private int disguiseTurnsLeft;
    private char displayChar;

    public StateData() {
        this.idleTurnCount = 0;
        this.disguiseTurnsLeft = 0;
        this.displayChar = 'M';
    }

    public int getIdleTurnCount() {
        return idleTurnCount;
    }

    public void incrementIdleTurns() {
        idleTurnCount++;
    }

    public void resetIdleTurns() {
        idleTurnCount = 0;
    }

    public int getDisguiseTurnsLeft() {
        return disguiseTurnsLeft;
    }

    public void setDisguiseTurnsLeft(int turns) {
        this.disguiseTurnsLeft = turns;
    }

    public void decrementDisguiseTurns() {
        if (disguiseTurnsLeft > 0) {
            disguiseTurnsLeft--;
        }
    }

    public char getDisplayChar() {
        return displayChar;
    }

    public void setDisplayChar(char c) {
        this.displayChar = c;
    }
}
