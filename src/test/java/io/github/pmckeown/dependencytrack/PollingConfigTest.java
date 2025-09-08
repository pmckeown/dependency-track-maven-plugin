package io.github.pmckeown.dependencytrack;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

public class PollingConfigTest {

    @Test
    public void thatSecondsIsAValidTimeUnitOptions() {
        PollingConfig pc = new PollingConfig(true, 1, 1, PollingConfig.TimeUnit.SECONDS);
        assertThat(pc.getChronoUnit(), is(equalTo(ChronoUnit.SECONDS)));
    }

    @Test
    public void thatMillisIsAValidTimeUnitOptions() {
        PollingConfig pc = new PollingConfig(true, 1, 1, PollingConfig.TimeUnit.MILLIS);
        assertThat(pc.getChronoUnit(), is(equalTo(ChronoUnit.MILLIS)));
    }
}
