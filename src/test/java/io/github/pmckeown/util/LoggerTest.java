package io.github.pmckeown.util;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
class LoggerTest {

    private static final String CONTENT = "interesting log stuff";

    @InjectMocks
    private Logger candidate;

    @Mock
    private Log log;

    @Test
    void thatWhenInfoIsEnabledLogIsInvoked() {
        doReturn(true).when(log).isInfoEnabled();

        candidate.info(CONTENT);

        verify(log).info(CONTENT);
    }

    @Test
    void thatWhenInfoIsDisabledLogIsNotInvoked() {
        doReturn(false).when(log).isInfoEnabled();

        candidate.info(CONTENT);

        verify(log, Mockito.never()).info(CONTENT);
    }

    @Test
    void thatWhenWarningIsEnabledLogIsInvoked() {
        doReturn(true).when(log).isWarnEnabled();

        candidate.warn(CONTENT);

        verify(log).warn(CONTENT);
    }

    @Test
    void thatWhenWarnIsDisabledLogIsNotInvoked() {
        doReturn(false).when(log).isWarnEnabled();

        candidate.warn(CONTENT);

        verify(log, Mockito.never()).warn(CONTENT);
    }

    @Test
    void thatWhenErrorIsEnabledLogIsInvoked() {
        doReturn(true).when(log).isErrorEnabled();

        candidate.error(CONTENT);

        verify(log).error(CONTENT);
    }

    @Test
    void thatWhenErrorIsDisabledLogIsNotInvoked() {
        doReturn(false).when(log).isErrorEnabled();

        candidate.error(CONTENT);

        verify(log, Mockito.never()).error(CONTENT);
    }

    @Test
    void thatWhenDebugIsEnabledLogIsInvoked() {
        doReturn(true).when(log).isDebugEnabled();

        candidate.debug(CONTENT);

        verify(log).debug(CONTENT);
    }

    @Test
    void thatWhenDebugIsDisabledLogIsNotInvoked() {
        doReturn(false).when(log).isDebugEnabled();

        candidate.debug(CONTENT);

        verify(log, Mockito.never()).debug(CONTENT);
    }

    @Test
    void thatWhenNoLoggerIsSuppliedAnExceptionIsThrown() {
        try {
            new Logger(null);
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(IllegalStateException.class)));
        }
    }
}
