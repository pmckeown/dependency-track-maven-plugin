package io.github.pmckeown.dependencytrack;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ThresholdsTest {

    @Test
    void thatIsEmptyReturnsFalseWhenNoValuesAreSet() {
        Thresholds thresholds = new Thresholds(null, null, null, null, null);
        assertTrue(thresholds.isEmpty());
    }

    @Test
    void thatIsEmptyReturnsTrueWhenACriticalValueIsSet() {
        Thresholds thresholds = new Thresholds(1, null, null, null, null);
        assertFalse(thresholds.isEmpty());
    }

    @Test
    void thatIsEmptyReturnsTrueWhenAHighValueIsSet() {
        Thresholds thresholds = new Thresholds(null, 1, null, null, null);
        assertFalse(thresholds.isEmpty());
    }

    @Test
    void thatIsEmptyReturnsTrueWhenAMediumValueIsSet() {
        Thresholds thresholds = new Thresholds(1, null, 1, null, null);
        assertFalse(thresholds.isEmpty());
    }

    @Test
    void thatIsEmptyReturnsTrueWhenALowValueIsSet() {
        Thresholds thresholds = new Thresholds(1, null, null, 1, null);
        assertFalse(thresholds.isEmpty());
    }

    @Test
    void thatIsEmptyReturnsTrueWhenAUnassignedValueIsSet() {
        Thresholds thresholds = new Thresholds(1, null, null, null, 1);
        assertFalse(thresholds.isEmpty());
    }
}
