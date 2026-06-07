package req4;

import game.grounds.CrackedFloor;
import game.grounds.Floor;
import game.grounds.RubbleField;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CrackableGroundTest {

    @Test
    void degradationLevelsIncreaseAcrossTheThreeGroundStates() {
        assertEquals(0, new Floor().getDegradationLevel(), "Floor should start at degradation level 0");
        assertEquals(1, new CrackedFloor().getDegradationLevel(), "CrackedFloor should be level 1");
        assertEquals(2, new RubbleField().getDegradationLevel(), "RubbleField should be level 2");
    }
}