package io.github.pmckeown.dependencytrack;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ThresholdsTest {

    @Test
    public void thatIsEmptyReturnsFalseWhenNoValuesAreSet() {
        Thresholds thresholds = new Thresholds(null, null, null, null, null);
        assertTrue(thresholds.isEmpty());
    }

    @Test
    public void thatIsEmptyReturnsTrueWhenACriticalValueIsSet() {
        Thresholds thresholds = new Thresholds(1, null, null, null, null);
        assertFalse(thresholds.isEmpty());
    }

    @Test
    public void thatIsEmptyReturnsTrueWhenAHighValueIsSet() {
        Thresholds thresholds = new Thresholds(null, 1, null, null, null);
        assertFalse(thresholds.isEmpty());
    }

    @Test
    public void thatIsEmptyReturnsTrueWhenAMediumValueIsSet() {
        Thresholds thresholds = new Thresholds(1, null, 1, null, null);
        assertFalse(thresholds.isEmpty());
    }

    @Test
    public void thatIsEmptyReturnsTrueWhenALowValueIsSet() {
        Thresholds thresholds = new Thresholds(1, null, null, 1, null);
        assertFalse(thresholds.isEmpty());
    }

    @Test
    public void thatIsEmptyReturnsTrueWhenAUnassignedValueIsSet() {
        Thresholds thresholds = new Thresholds(1, null, null, null, 1);
        assertFalse(thresholds.isEmpty());
    }
}
