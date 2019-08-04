package io.github.pmckeown.dependencytrack.upload;

import org.junit.Test;

import static org.junit.Assert.fail;

public class SleeperTest {

    private Sleeper sleeper = new Sleeper();

    @Test
    public void thatSleeperDoesNotError() {
        try {
            sleeper.sleep(1);
        } catch (Exception ex) {
            fail("Exception not expected");
        }
    }
}
