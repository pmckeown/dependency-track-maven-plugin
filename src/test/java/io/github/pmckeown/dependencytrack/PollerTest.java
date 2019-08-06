package io.github.pmckeown.dependencytrack;

import com.evanlennick.retry4j.exception.RetriesExhaustedException;
import com.evanlennick.retry4j.exception.UnexpectedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static io.github.pmckeown.dependencytrack.PollingConfig.TimeUnit.MILLIS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class PollerTest {

    @Test
    public void thatThatPopulatedOptionalExitsThePollingLoop() {
        String returnValue = "Returned from callable";

        Poller<String> poller = new Poller<>();

        Optional<String> optionalString = poller.poll(PollingConfig.defaults(), () -> Optional.of(returnValue));

        if (!optionalString.isPresent()) {
            fail("No random string generated");
        } else {
            assertThat(optionalString.get(), is(equalTo(returnValue)));
        }
    }

    @Test
    public void thatThatEmptyOptionalLoopsTheMaximumNumberOfTimesThenThrowsException() {
        PollingConfig pollingConfig = new PollingConfig(true, 1 ,5, MILLIS);
        Poller<String> poller = new Poller<>();
        final int[] pollLoopCounter = {0};

        try {
            Optional<String> optionalString = poller.poll(pollingConfig, () -> {
                    pollLoopCounter[0]++;
                    return Optional.empty();
            });
            fail("RetriesExhaustedException expected");
        } catch (Exception ex) {
            assertThat(ex, instanceOf(RetriesExhaustedException.class));
        }
        assertThat(pollLoopCounter[0], is(equalTo(pollingConfig.getAttempts())));
    }

    @Test
    public void thatThatExceptionDuringPollingExitsWithException() {
        PollingConfig pollingConfig = new PollingConfig(true, 1 ,1, MILLIS);
        Poller<String> poller = new Poller<>();

        try {
            poller.poll(pollingConfig, () -> {
                throw new DependencyTrackException("Boom");
            });
            fail("UnexpectedException expected");
        } catch (Exception ex) {
            assertThat(ex, instanceOf(UnexpectedException.class));
            assertThat(ex.getCause(), instanceOf(DependencyTrackException.class));
        }
    }

    @Test
    public void IfPollingDisabledTheCallableIsExecutedOnlyOnce() {
        PollingConfig pollingConfig = new PollingConfig(false, 1 ,5, MILLIS);
        Poller<String> poller = new Poller<>();
        final int[] pollLoopCounter = {0};

        try {
            Optional<String> optionalString = poller.poll(pollingConfig, () -> {
                pollLoopCounter[0]++;
                return Optional.empty();
            });
            fail("RetriesExhaustedException expected");
        } catch (Exception ex) {
            assertThat(ex, instanceOf(RetriesExhaustedException.class));
        }
        assertThat(pollLoopCounter[0], is(not(equalTo(pollingConfig.getAttempts()))));
        assertThat(pollLoopCounter[0], is(equalTo(1)));
    }
}
