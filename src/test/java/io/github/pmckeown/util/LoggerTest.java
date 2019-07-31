package io.github.pmckeown.util;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LoggerTest {

    private static final String CONTENT = "interesting log stuff";

    @InjectMocks
    private Logger candidate;

    @Mock
    private Log log;

    @Test
    public void thatWhenInfoIsEnabledLogIsInvoked() {
        doReturn(true).when(log).isInfoEnabled();

        candidate.info(CONTENT);

        verify(log).info(CONTENT);
    }

    @Test
    public void thatWhenInfoIsDisabledLogIsNotInvoked() {
        doReturn(false).when(log).isInfoEnabled();

        candidate.info(CONTENT);

        verify(log, Mockito.never()).info(CONTENT);
    }

    @Test
    public void thatWhenWarningIsEnabledLogIsInvoked() {
        doReturn(true).when(log).isWarnEnabled();

        candidate.warn(CONTENT);

        verify(log).warn(CONTENT);
    }

    @Test
    public void thatWhenWarnIsDisabledLogIsNotInvoked() {
        doReturn(false).when(log).isWarnEnabled();

        candidate.warn(CONTENT);

        verify(log, Mockito.never()).warn(CONTENT);
    }

    @Test
    public void thatWhenErrorIsEnabledLogIsInvoked() {
        doReturn(true).when(log).isErrorEnabled();

        candidate.error(CONTENT);

        verify(log).error(CONTENT);
    }

    @Test
    public void thatWhenErrorIsDisabledLogIsNotInvoked() {
        doReturn(false).when(log).isErrorEnabled();

        candidate.error(CONTENT);

        verify(log, Mockito.never()).error(CONTENT);
    }

    @Test
    public void thatWhenDebugIsEnabledLogIsInvoked() {
        doReturn(true).when(log).isDebugEnabled();

        candidate.debug(CONTENT);

        verify(log).debug(CONTENT);
    }

    @Test
    public void thatWhenDebugIsDisabledLogIsNotInvoked() {
        doReturn(false).when(log).isDebugEnabled();

        candidate.debug(CONTENT);

        verify(log, Mockito.never()).debug(CONTENT);
    }

    @Test
    public void thatWhenNoLoggerIsSuppliedAnExceptionIsThrown() {
        try {
            new Logger(null);
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(IllegalStateException.class)));
        }
    }

}
