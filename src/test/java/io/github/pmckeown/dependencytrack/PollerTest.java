package io.github.pmckeown.dependencytrack;

import static io.github.pmckeown.dependencytrack.PollingConfig.TimeUnit.MILLIS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import com.evanlennick.retry4j.exception.RetriesExhaustedException;
import com.evanlennick.retry4j.exception.UnexpectedException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
class PollerTest {

    @Test
    void thatThatPopulatedOptionalExitsThePollingLoop() {
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
    void thatThatEmptyOptionalLoopsTheMaximumNumberOfTimesThenThrowsException() {
        PollingConfig pollingConfig = new PollingConfig(true, 1, 5, MILLIS);
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
    void thatThatExceptionDuringPollingExitsWithException() {
        PollingConfig pollingConfig = new PollingConfig(true, 1, 1, MILLIS);
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
    void IfPollingDisabledTheCallableIsExecutedOnlyOnce() {
        PollingConfig pollingConfig = new PollingConfig(false, 1, 5, MILLIS);
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
